import sbt._

val appName = "hmrctest"

lazy val microservice = Project(appName, file("."))
  .enablePlugins(SbtAutoBuildPlugin, SbtGitVersioning, SbtAutoBuildPlugin)
  .settings(
    majorVersion := 3,
    scalaVersion := "2.11.12",
    makePublicallyAvailableOnBintray := true,
    libraryDependencies ++= LibDependencies.compile
  )
