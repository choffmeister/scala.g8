import scalariform.formatter.preferences._
import de.johoop.jacoco4sbt._
import JacocoPlugin._


name := "$name$"

version := "$version$"

organization := "$organization$"

scalaVersion := "$scalaversion$"

scalacOptions := Seq("-encoding", "utf8")

libraryDependencies ++= Seq(
  "ch.qos.logback" % "logback-classic" % "1.0.13",
  "com.typesafe" % "config" % "1.2.0",
  "com.typesafe.akka" %% "akka-actor" % "2.3.6",
  "com.typesafe.akka" %% "akka-slf4j" % "2.3.6",
  "com.typesafe.akka" %% "akka-testkit" % "2.3.6" % "test",
  "org.specs2" %% "specs2" % "2.3.11" % "test"
)


packSettings

packMain := Map("$name$" -> "$package$.Application")


scalariformSettings

ScalariformKeys.preferences := ScalariformKeys.preferences.value
  .setPreference(RewriteArrowSymbols, true)
  .setPreference(SpacesWithinPatternBinders, true)
  .setPreference(CompactControlReadability, false)


jacoco.settings

jacoco.reportFormats in jacoco.Config := Seq(
  XMLReport(encoding = "utf-8"),
  ScalaHTMLReport(withBranchCoverage = true)
)
