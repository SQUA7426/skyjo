FROM ligadigital/scala-sbt:scala-2.13-java-21
WORKDIR /skyjo

# Kopiere alle Projektdateien ins Image
COPY . /skyjo

# SBT lädt alle Dependencies beim ersten Build
RUN sbt compile

# Starte das Projekt
CMD ["sbt", "run"]
