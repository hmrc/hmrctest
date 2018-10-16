import play.core.PlayVersion
import play.sbt.PlayImport.ws
import sbt._

private object LibDependencies {
  val compile: Seq[ModuleID] = Seq(
    ws % "provided",
    "org.scalatest" %% "scalatest" % "3.0.5",
    "com.typesafe.play" %% "play-test" % PlayVersion.current,
    "ch.qos.logback" % "logback-classic" % "1.2.3",
    "org.pegdown" % "pegdown" % "1.6.0"
  )
}
