import PlayCrossCompilation._

val libName = "hmrctest"

lazy val library = Project(libName, file("."))
  .enablePlugins(SbtAutoBuildPlugin, SbtGitVersioning, SbtArtifactory)
  .settings(
    majorVersion := 3,
    scalaVersion := "2.11.12",
    crossScalaVersions := List("2.11.12", "2.12.8"),
    makePublicallyAvailableOnBintray := true,
    libraryDependencies ++= LibDependencies.compile,
    playCrossCompilationSettings
  )
