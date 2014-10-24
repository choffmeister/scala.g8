package $package$.http

import de.choffmeister.auth.common.OAuth2AccessTokenResponseFormat
import spray.httpx._
import spray.json._

trait JsonProtocol extends DefaultJsonProtocol
    with SprayJsonSupport {
  implicit val oauth2AccessTokenResponseFormat = OAuth2AccessTokenResponseFormat
}
