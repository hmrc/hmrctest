import sbt._

private object LibDependencies {
  private val play25Version = "2.5.19"
  private val play26Version = "2.6.21"
  
  val compile: Seq[ModuleID] = PlayCrossCompilation.dependencies(
    shared = Seq(
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "org.pegdown" % "pegdown" % "1.6.0",
      // force dependencies due to security flaws found in jackson-databind < 2.9.x using XRay
      "com.fasterxml.jackson.core"     % "jackson-core"            % "2.9.8",
      "com.fasterxml.jackson.core"     % "jackson-databind"        % "2.9.8",
      "com.fasterxml.jackson.core"     % "jackson-annotations"     % "2.9.8",
      "com.fasterxml.jackson.datatype" % "jackson-datatype-jdk8"   % "2.9.8",
      "com.fasterxml.jackson.datatype" % "jackson-datatype-jsr310" % "2.9.8"
    ),
    play25 = Seq(
      "com.typesafe.play" %% "play-ws" % play25Version % "provided",
      "com.typesafe.play" %% "play-test" % play25Version,
      "org.scalatest" %% "scalatest" % "3.0.5",
      // force dependencies due to security flaws found in xercesImpl 2.11.0
      // only applies to play 2.5 since it was removed from play 2.6
      // https://github.com/playframework/playframework/blob/master/documentation/manual/releases/release26/migration26/Migration26.md#xercesimpl-removal
      "xerces" % "xercesImpl" % "2.12.0"
    ),
    play26 = Seq(
      "com.typesafe.play" %% "play-ahc-ws" % play26Version % "provided",
      "com.typesafe.play" %% "play-test" % play26Version,
      "org.scalatest" %% "scalatest" % "3.0.5"
    )
  )
}
