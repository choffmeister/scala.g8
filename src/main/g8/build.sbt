name := "$name$"

version := "$version$"

organization := "$organization$"

scalaVersion := "2.11.0"

scalacOptions := Seq("-unchecked", "-feature", "-deprecation", "-language:postfixOps", "-encoding", "utf8")

libraryDependencies ++= Seq(
  "org.specs2" %% "specs2" % "2.3.11" % "test"
)
