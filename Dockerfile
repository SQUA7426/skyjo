# syntax=docker/dockerfile:1.7

FROM eclipse-temurin:21-jdk-jammy AS builder

RUN --mount=type=cache,target=/var/cache/apt,sharing=locked \
    --mount=type=cache,target=/var/lib/apt,sharing=locked \
    apt-get update && apt-get install -y --no-install-recommends \
        curl gnupg ca-certificates apt-transport-https \
    && mkdir -p /etc/apt/keyrings \
    && curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x2EE0EA64E40A89B84B2DF73499E82A75642AC823" \
       | gpg --dearmor -o /etc/apt/keyrings/scalasbt.gpg \
    && echo "deb [signed-by=/etc/apt/keyrings/scalasbt.gpg] https://repo.scala-sbt.org/scalasbt/debian all main" \
       > /etc/apt/sources.list.d/sbt.list \
    && apt-get update \
    && apt-get install -y --no-install-recommends sbt \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

ENV SBT_OPTS="-Xms512m -Xmx2g -XX:MaxMetaspaceSize=512m"

COPY build.sbt ./
COPY project/ ./project/

RUN --mount=type=cache,target=/root/.ivy2,sharing=locked \
    --mount=type=cache,target=/root/.cache/coursier,sharing=locked \
    --mount=type=cache,target=/root/.sbt,sharing=locked \
    sbt -v -batch -Dsbt.server.forcestart=false update

COPY src/ ./src/

RUN --mount=type=cache,target=/root/.ivy2,sharing=locked \
    --mount=type=cache,target=/root/.cache/coursier,sharing=locked \
    --mount=type=cache,target=/root/.sbt,sharing=locked \
    sbt -v -batch -Dsbt.server.forcestart=false "clean; assembly" \
    && cp target/scala-3.3.7/skyjo-assembly.jar /tmp/app.jar

FROM eclipse-temurin:21-jre-jammy

RUN apt-get update && apt-get install -y --no-install-recommends \
        libx11-6 \
        libx11-xcb1 \
        libxext6 \
        libxrender1 \
        libxi6 \
        libxtst6 \
        libxxf86vm1 \
        libgl1 \
        libgtk-3-0 \
        libfreetype6 \
        libfontconfig1 \
        fonts-dejavu-core \
        libasound2 \
    && rm -rf /var/lib/apt/lists/*

RUN groupadd -r skyjo \
 && useradd -r -g skyjo -m -d /home/skyjo skyjo \
 && mkdir -p /home/skyjo/app/saves \
 && chown -R skyjo:skyjo /home/skyjo

USER skyjo
WORKDIR /home/skyjo/app

COPY --from=builder /tmp/app.jar ./app.jar

VOLUME ["/home/skyjo/app/saves"]

ENTRYPOINT ["java", "-Djava.awt.headless=false", "-Dprism.order=sw", "-Dglass.gtk.uiScale=1.0", "-jar", "/home/skyjo/app/app.jar"]
