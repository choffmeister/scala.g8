package $package$.http

import akka.actor._
import akka.io.Tcp._
import spray.http.HttpMethods._
import spray.http.StatusCodes._
import spray.http._

class HttpServiceActor(apiHttp: ActorRef, staticContentHttp: ActorRef) extends Actor with ActorLogging {
  def receive = {
    case Connected(_, _) =>
      sender ! Register(self)
    case req@HttpRequest(_, uri, _, _, _) if uri.path.startsWith(Uri.Path("/api")) =>
      apiHttp.tell(req, sender)
    case req@HttpRequest(GET, _, _, _, _) =>
      staticContentHttp.tell(req, sender)
    case req@HttpRequest(_, _, _, _, _) =>
      sender ! HttpResponse(status = BadRequest)
  }
}
