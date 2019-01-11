import sbt._

private object LibDependencies {
  private val play25Version = "2.5.19"
  private val play26Version = "2.6.21"
  
  val compile: Seq[ModuleID] = PlayCrossCompilation.dependencies(
    shared = Seq(
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "org.pegdown" % "pegdown" % "1.6.0"
    ),
    play25 = Seq(
      "com.typesafe.play" %% "play-ws" % play25Version % "provided",
      "com.typesafe.play" %% "play-test" % play25Version,
      "org.scalatest" %% "scalatest" % "3.0.5"
    ),
    play26 = Seq(
      "com.typesafe.play" %% "play-ahc-ws" % play26Version % "provided",
      "com.typesafe.play" %% "play-test" % play26Version,
      "org.scalatest" %% "scalatest" % "3.0.5"
    )
  )
}
