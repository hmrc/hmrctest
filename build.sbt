import sbt._

val libName = "hmrctest"

lazy val library = Project(libName, file("."))
  .enablePlugins(SbtAutoBuildPlugin, SbtGitVersioning, SbtAutoBuildPlugin)
  .settings(
    majorVersion := 3,
    scalaVersion := "2.11.12",
    makePublicallyAvailableOnBintray := true,
    libraryDependencies ++= LibDependencies.compile
  )
