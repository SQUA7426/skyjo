val scala3Version = "3.3.7"

ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := scala3Version
ThisBuild / javacOptions += "--release=21"
ThisBuild / scalacOptions ++= Seq("-release:21")

libraryDependencies ++= Seq(
  "org.scalafx" %% "scalafx" % "21.0.0-R32",
  "org.scalactic" %% "scalactic" % "3.2.14",
  "org.scalatest" %% "scalatest" % "3.2.10" % Test,
  "org.scalameta" %% "munit" % "1.0.0"
)

lazy val root = (project in file("."))
  .settings(
    name := "skyjo"
  )
