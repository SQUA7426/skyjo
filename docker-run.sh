#!/usr/bin/env bash
# docker-run.sh – Launch the Skyjo ScalaFX GUI container.
#
# Works on:
#   Linux  – forwards the local X11 socket directly (fastest)
#   macOS  – forwards through XQuartz (install from https://www.xquartz.org)
#
# Usage:
#   ./docker-run.sh              # run normally
#   ./docker-run.sh --rebuild    # force a fresh image build first
# ─────────────────────────────────────────────────────────────────────────────
set -euo pipefail

IMAGE_NAME="skyjo"
CONTAINER_NAME="skyjo-game"
SAVES_DIR="$(pwd)/saves"

# ── Optional rebuild ──────────────────────────────────────────────────────────
if [[ "${1:-}" == "--rebuild" ]]; then
  echo "==> Rebuilding image …"
  DOCKER_BUILDKIT=1 docker build -t "$IMAGE_NAME" .
fi

# ── Detect platform and configure X11 forwarding ─────────────────────────────
X11_VOLUME=""
DRI_DEVICE=""
EXTRA_OPTS=""

case "$(uname -s)" in

  Linux)
    # Allow the Docker daemon (running as root) to connect to the user's display.
    xhost +local:docker 2>/dev/null || true

    DISPLAY_ENV="${DISPLAY:-:0}"
    X11_VOLUME="-v /tmp/.X11-unix:/tmp/.X11-unix:ro"

    # Mount GPU/DRI device only if it exists (omitting it is safe; JavaFX falls
    # back to software rendering via -Dprism.order=sw,es2,j2d in the image).
    if [[ -e /dev/dri ]]; then
      DRI_DEVICE="--device /dev/dri:/dev/dri"
    fi
    ;;

  Darwin)
    # macOS: XQuartz must be running.
    # Enable "Allow connections from network clients" in
    # XQuartz → Preferences → Security.
    if ! pgrep -x Xquartz &>/dev/null; then
      echo "ERROR: XQuartz does not appear to be running."
      echo "       Install from https://www.xquartz.org and launch it first."
      exit 1
    fi

    HOST_IP=$(ipconfig getifaddr en0 2>/dev/null \
              || ifconfig | awk '/inet /{print $2}' | grep -v 127 | head -1)
    if [[ -z "${HOST_IP:-}" ]]; then
      echo "ERROR: Could not determine host IP address for XQuartz."
      exit 1
    fi
    xhost + "$HOST_IP" 2>/dev/null || true

    DISPLAY_ENV="${HOST_IP}:0"
    EXTRA_OPTS="--add-host=host.docker.internal:host-gateway"
    ;;

  *)
    echo "ERROR: Unsupported platform '$(uname -s)'."
    echo "       On Windows, use WSLg or VcXsrv from within WSL2."
    exit 1
    ;;
esac

# ── Ensure saves directory exists on the host ─────────────────────────────────
mkdir -p "$SAVES_DIR"

# ── Build docker run arguments array ─────────────────────────────────────────
RUN_ARGS=(
  --rm -it
  --name "$CONTAINER_NAME"
  -e "DISPLAY=$DISPLAY_ENV"
  -e "XDG_RUNTIME_DIR=${XDG_RUNTIME_DIR:-/tmp}"
  -v "$SAVES_DIR:/home/skyjo/app/saves"
)

[[ -n "$X11_VOLUME"  ]] && RUN_ARGS+=($X11_VOLUME)
[[ -n "$DRI_DEVICE"  ]] && RUN_ARGS+=($DRI_DEVICE)
[[ -n "$EXTRA_OPTS"  ]] && RUN_ARGS+=($EXTRA_OPTS)

# ── Run ───────────────────────────────────────────────────────────────────────
echo "==> Starting Skyjo GUI (container: $CONTAINER_NAME)"
echo "    DISPLAY : $DISPLAY_ENV"
echo "    saves/  : $SAVES_DIR"

docker run "${RUN_ARGS[@]}" "$IMAGE_NAME"
