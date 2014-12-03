package $package$.http

import $package$._

import akka.actor._
import spray.routing._

class ApiHttpService extends Actor with HttpService with JsonProtocol {
  implicit val actorRefFactory = context
  implicit val executor = context.dispatcher

  def receive = runRoute(route)
  def route = pathPrefix("api") {
    pathPrefix("auth") {
      pathPrefix("token") {
        path("create") {
          authenticate(Auth.basicHttpAuthenticator) { user ⇒
            complete(Auth.createTokenResponse(user))
          }
        }
      } ~
        authenticate(Auth.httpAuthenticator) { user ⇒
          path("info") {
            complete(user)
          }
        }
    }
  }
}
