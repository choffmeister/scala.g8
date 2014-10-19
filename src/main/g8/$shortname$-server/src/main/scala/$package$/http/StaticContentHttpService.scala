package $package$.http

import java.io._

import akka.actor._
import spray.routing._

class StaticContentHttpService(contentDir: Option[File]) extends Actor with HttpService {
  implicit val actorRefFactory = context
  implicit val executor = context.dispatcher
  implicit val timeout = akka.util.Timeout(1000)

  def receive = runRoute(route)
  def route = contentDir match {
    case Some(contentDir) ⇒
      val index = getFromFile(s"\${contentDir}/index.html")
      val cache = getFromFile(s"\${contentDir}/cache.manifest")
      val app = getFromDirectory(contentDir.toString)
      pathSingleSlash(index) ~
        path("index.html")(index) ~
        path("cache.manifest")(cache) ~
        pathPrefixTest(("app" ~ Slash))(app) ~
        pathPrefixTest(!("app" ~ Slash))(index)
    case _ ⇒
      reject()
  }
}
