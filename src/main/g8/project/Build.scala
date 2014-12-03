import sbt._
import sbt.Keys._
import xerial.sbt.Pack.{ pack => sbtPack }
import de.choffmeister.sbt.WebAppPlugin.{ webAppBuild => sbtWebAppBuild }

object Build extends sbt.Build {
  lazy val dist = TaskKey[File]("dist", "Builds the distribution packages")
  lazy val dev = TaskKey[File]("dev", "Runs the backend and the frontend")

  lazy val buildSettings = Seq(
    scalaVersion := "$scalaversion$",
    scalacOptions ++= Seq("-encoding", "utf8"))

  lazy val coordinateSettings = Seq(
    organization := "$organization$",
    version := "$version$")

  lazy val commonSettings = Defaults.defaultSettings ++ coordinateSettings ++ buildSettings

  lazy val server = (project in file("$shortname$-server"))
    .settings(commonSettings: _*)
    .settings(Pack.settings: _*)
    .settings(Scalariform.settings: _*)
    .settings(Jacoco.settings: _*)
    .settings(libraryDependencies ++= Seq(
      "ch.qos.logback" % "logback-classic" % "1.0.13",
      "com.jcraft" % "jsch" % "0.1.50",
      "com.typesafe" % "config" % "1.2.0",
      "com.typesafe.akka" %% "akka-actor" % "2.3.6",
      "com.typesafe.akka" %% "akka-slf4j" % "2.3.6",
      "com.typesafe.akka" %% "akka-testkit" % "2.3.6" % "test",
      "commons-codec" % "commons-codec" % "1.9",
      "de.choffmeister" %% "auth-common" % "0.0.1",
      "de.choffmeister" %% "auth-spray" % "0.0.1",
      "io.spray" %% "spray-can" % "1.3.1",
      "io.spray" %% "spray-json" % "1.2.6",
      "io.spray" %% "spray-routing" % "1.3.1",
      "io.spray" %% "spray-testkit" % "1.3.1" % "test",
      "org.specs2" %% "specs2" % "2.4.1" % "test"))
    .settings(name := "$name$-server")

  lazy val web = (project in file("$shortname$-web"))
    .settings(commonSettings: _*)
    .settings(WebApp.settings: _*)
    .settings(name := "$name$-web")

  lazy val root = (project in file("."))
    .settings(coordinateSettings: _*)
    .settings(name := "$name$")
    .settings(dist <<= (streams, target, sbtPack in server, sbtWebAppBuild in web) map { (s, target, server, web) =>
      val distDir = target / "dist"
      val distBinDir = distDir / "bin"
      val distWebDir = distDir / "web"
      IO.copyDirectory(server, distDir)
      IO.copyDirectory(web, distWebDir)
      distBinDir.listFiles.foreach(_.setExecutable(true, false))
      distDir
    })
    .aggregate(server, web)
}

object Pack {
  import xerial.sbt.Pack._

  lazy val settings = packSettings ++ Seq(
    packMain := Map("server" -> "$package$.Server"),
    packExtraClasspath := Map("server" -> Seq("\${PROG_HOME}/conf")))
}

object WebApp {
  import de.choffmeister.sbt.WebAppPlugin._

  lazy val settings = webAppSettings
}

object Jacoco {
  import de.johoop.jacoco4sbt._
  import JacocoPlugin._

  lazy val settings = jacoco.settings ++ reports

  lazy val reports = Seq(
    jacoco.reportFormats in jacoco.Config := Seq(
      XMLReport(encoding = "utf-8"),
      ScalaHTMLReport(withBranchCoverage = true)))
}

object Scalariform {
  import com.typesafe.sbt._
  import com.typesafe.sbt.SbtScalariform._
  import scalariform.formatter.preferences._

  lazy val settings = SbtScalariform.scalariformSettings ++ preferences

  lazy val preferences = Seq(
    ScalariformKeys.preferences := ScalariformKeys.preferences.value
      .setPreference(RewriteArrowSymbols, true)
      .setPreference(SpacesWithinPatternBinders, true)
      .setPreference(CompactControlReadability, false))
}
