package $package$.http

import $package$._

import akka.actor._
import de.choffmeister.auth.common.JsonWebToken
import spray.http.StatusCodes.Unauthorized
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
        } ~
          path("renew") {
            authenticate(Auth.bearerTokenHttpAuthenticator.withoutExpiration) { user ⇒
              complete(Auth.createTokenResponse(user))
            }
          }
      } ~
        path("info") {
          authenticate(Auth.httpAuthenticator) { user ⇒
            complete(user)
          }
        }
    }
  }
}
