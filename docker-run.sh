#!/bin/bash

xhost +local:docker

docker run -it --rm \
  --net=host \
  -e DISPLAY=$DISPLAY \
  -v /tmp/.X11-unix:/tmp/.X11-unix \
  -e XDG_RUNTIME_DIR=$XDG_RUNTIME_DIR \
  --device /dev/dri:/dev/dri \
  skyjo
