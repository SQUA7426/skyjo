import sbtassembly.AssemblyPlugin.autoImport._
import sbtassembly.{MergeStrategy, PathList}

val scala3Version = "3.3.7"
val javaFxVersion = "21"

lazy val osName = System.getProperty("os.name") match {
  case n if n.startsWith("Linux")   => "linux"
  case n if n.startsWith("Mac")     => "mac"
  case n if n.startsWith("Windows") => "win"
  case _                            => throw new Exception("Unknown platform!")
}

ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := scala3Version
ThisBuild / javacOptions ++= Seq("--release", "21")
ThisBuild / scalacOptions ++= Seq("-release", "21")

Compile / run / fork := true

libraryDependencies ++= Seq(
  "org.scalafx"            %% "scalafx"               % "21.0.0-R32",
  "org.scalactic"          %% "scalactic"             % "3.2.14",
  "org.scalatest"          %% "scalatest"             % "3.2.14" % Test,
  "org.scalameta"          %% "munit"                 % "1.0.0"  % Test,
  "org.apache.commons"      % "commons-lang3"         % "3.4",
  "commons-io"              % "commons-io"            % "1.3.2",
  "org.scala-lang.modules" %% "scala-xml"             % "2.3.0",
  "org.playframework"      %% "play-json"             % "3.0.6",
  "com.google.inject"       % "guice"                 % "7.0.0",
  "net.codingwell"         %% "scala-guice"           % "7.0.0",
  "jakarta.inject"          % "jakarta.inject-api"    % "2.0.1"
)

libraryDependencies ++=
  Seq("base", "controls", "fxml", "graphics", "media", "swing", "web")
    .map(m => "org.openjfx" % s"javafx-$m" % javaFxVersion classifier osName)

coverageEnabled                 := false
coverageHighlighting            := true
coverageFailOnMinimum           := false
coverageMinimumStmtTotal        := 95
coverageMinimumBranchTotal      := 100
coverageMinimumStmtPerPackage   := 100
coverageMinimumBranchPerPackage := 100
coverageMinimumStmtPerFile      := 100
coverageMinimumBranchPerFile    := 100
coverageExcludedPackages        := ".*Main.*"

Compile / mainClass := Some("de.htwg.se.skyjo.Main")
assembly / mainClass := Some("de.htwg.se.skyjo.Main")

assembly / assemblyJarName := "skyjo-assembly.jar"

assembly / test := {}

assembly / assemblyMergeStrategy := {
  case PathList("META-INF", "MANIFEST.MF") =>
    MergeStrategy.discard

  case PathList("META-INF", xs @ _*)
      if xs.nonEmpty && (
        xs.last.endsWith(".SF")  ||
        xs.last.endsWith(".DSA") ||
        xs.last.endsWith(".RSA")
      ) =>
    MergeStrategy.discard

  case PathList("module-info.class") =>
    MergeStrategy.discard

  case PathList("META-INF", "versions", "9", "module-info.class") =>
    MergeStrategy.discard

  case PathList("META-INF", "substrate", "config", _ @ _*) =>
    MergeStrategy.first

  case x =>
    val oldStrategy = (assembly / assemblyMergeStrategy).value
    oldStrategy(x)
}

lazy val root = (project in file("."))
  .settings(
    name := "skyjo"
  )
