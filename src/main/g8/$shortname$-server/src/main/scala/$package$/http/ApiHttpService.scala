package $package$.http

import akka.actor._
import spray.routing._

class ApiHttpService extends Actor with HttpService {
  implicit val actorRefFactory = context
  implicit val executor = context.dispatcher

  def receive = runRoute(route)
  def route = pathPrefix("api") {
    complete("$name$")
  }
}
