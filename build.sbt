val scala3Version = "3.3.7"

ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := scala3Version
ThisBuild / javacOptions += "--release=21"
ThisBuild / scalacOptions ++= Seq("-release:21")

libraryDependencies ++= Seq(
  "org.scalafx" %% "scalafx" % "21.0.0-R32",
  "org.scalactic" %% "scalactic" % "3.2.14",
  "org.scalatest" %% "scalatest" % "3.2.10" % Test,
  "org.scalameta" %% "munit" % "1.0.0",
  "org.apache.commons" % "commons-lang3" % "3.4",
  "org.apache.commons" % "commons-io" % "1.3.2"
)

coverageEnabled := true
coverageHighlighting := true
coverageFailOnMinimum := false

coverageMinimumStmtTotal := 95
coverageMinimumBranchTotal := 100
coverageMinimumStmtPerPackage := 100
coverageMinimumBranchPerPackage := 100
coverageMinimumStmtPerFile := 100
coverageMinimumBranchPerFile := 100
coverageExcludedPackages := ".*Main.*"

// --------------------- SCALAFX CONF ---------------------------------------//

val javaFxVersion = "21"

libraryDependencies ++= {
  lazy val osName = System.getProperty("os.name") match {
    case n if n.startsWith("Linux")   => "linux"
    case n if n.startsWith("Mac")     => "mac"
    case n if n.startsWith("Windows") => "win"
    case _                            => throw new Exception("Unknown platform!")
  }

  Seq("base", "controls", "fxml", "graphics", "media", "swing", "web")
    .map(m => "org.openjfx" % s"javafx-$m" % javaFxVersion classifier osName)
}

lazy val root = (project in file("."))
  .settings(
    name := "skyjo"
  )
