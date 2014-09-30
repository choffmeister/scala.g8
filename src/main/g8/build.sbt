import scalariform.formatter.preferences._
import de.johoop.jacoco4sbt._
import JacocoPlugin._


name := "$name$"

version := "$version$"

organization := "$organization$"

scalaVersion := "$scalaversion$"

scalacOptions := Seq("-encoding", "utf8")

libraryDependencies ++= Seq(
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
