# --- Stage 1: Build ---
FROM eclipse-temurin:21-jdk-jammy AS builder
WORKDIR /app

RUN apt-get update && apt-get install -y curl && \
    curl -L https://github.com/sbt/sbt/releases/download/v1.10.0/sbt-1.10.0.tgz | tar -xz -C /usr/local && \
    ln -s /usr/local/sbt/bin/sbt /usr/local/bin/sbt

COPY build.sbt ./
COPY project ./project
RUN sbt update

COPY . .

RUN mkdir -p /app/target/scala-3.3.7/scoverage-data/

RUN sbt clean coverageOff assembly

# --- Stage 2: Runtime ---
FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app

# Install runtime UI libraries (since Skyjo likely needs them)
RUN apt-get update && apt-get install -y --no-install-recommends \
    libgl1 libgtk-3-0 libx11-xcb1 libxtst6 libxrender1 libxi6 libfreetype6 libfontconfig1 \
    && rm -rf /var/lib/apt/lists/*

COPY --from=builder /app/target/scala-3.3.7/*assembly*.jar /app/app.jar

CMD ["java", "-jar", "/app/app.jar"]
