package $package$

import $package$.http._

import akka.actor._
import akka.io.IO
import spray.can.Http

import scala.concurrent.duration._

class Server extends Bootable {
  implicit val system = ActorSystem("$name$")
  implicit val executor = system.dispatcher

  lazy val config = Config.load()

  def startup() = {
    val apiHttp = system.actorOf(Props(new ApiHttpService()), "http-api")
    val staticContentHttp = system.actorOf(Props(new StaticContentHttpService(config.httpWebDir)), "http-staticcontent")
    val rootHttp = system.actorOf(Props(new HttpServiceActor(apiHttp, staticContentHttp)), "http-root")

    IO(Http) ! Http.Bind(rootHttp, interface = config.httpInterface, port = config.httpPort)
  }

  def shutdown() = {
    system.shutdown()
    system.awaitTermination(1.seconds)
  }
}

object Server {
  def main(args: Array[String]) {
    val server = new Server()
    server.startup()
  }
}

trait Bootable {
  def startup(): Unit
  def shutdown(): Unit

  sys.ShutdownHookThread(shutdown())
}
