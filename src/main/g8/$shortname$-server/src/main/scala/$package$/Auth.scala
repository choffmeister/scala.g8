package $package$

import java.util.Date

import de.choffmeister.auth.common._
import de.choffmeister.auth.spray._
import de.choffmeister.auth.spray.RichHttpAuthenticator._
import de.choffmeister.auth.spray.RichUserPassAuthenticator._
import com.typesafe.config._
import spray.json._
import spray.routing.authentication._

import scala.concurrent._
import scala.concurrent.duration.FiniteDuration
import scala.concurrent.ExecutionContext.Implicits.global

object Auth {
  lazy val config = Config.load()

  lazy val passwordHasher = new PasswordHasher(
    config.authPasswordsStorageDefaultAlgorithm,
    config.authPasswordsStorageDefaultAlgorithmConfig,
    PBKDF2 :: Plain :: Nil)

  lazy val userPassAuthenticator = new UserPassAuthenticator[String] {
    def apply(userPass: Option[UserPass]): Future[Option[String]] = Future(userPass.flatMap { up ⇒
      try {
        if (config.authUsers.contains(up.user)) {
          val storedPass = config.authUsers(up.user)
          if (passwordHasher.validate(storedPass, up.pass)) Some(up.user) else None
        } else None
      } catch {
        case _: Throwable ⇒ None
      }
    })
  }.delayed(config.authPasswordsValidationDelay)

  lazy val basicHttpAuthenticator = new BasicHttpAuthenticator(config.authRealm, userPassAuthenticator)
  lazy val bearerTokenHttpAuthenticator = new OAuth2BearerTokenHttpAuthenticator[String](config.authRealm, config.authTokenSecret, sub ⇒ Future(Some(sub)))
  lazy val httpAuthenticator = bearerTokenHttpAuthenticator.withFallback(basicHttpAuthenticator)

  def createTokenResponse(subject: String, claims: Map[String, String] = Map.empty): OAuth2AccessTokenResponse = {
    val now = System.currentTimeMillis
    val token = JsonWebToken(
      createdAt = new Date(now),
      expiresAt = new Date(now + config.authTokenLifetime.toMillis),
      subject = subject,
      claims = claims.map(c ⇒ c._1 -> JsString(c._2))
    )
    OAuth2AccessTokenResponse("bearer", JsonWebToken.write(token, config.authTokenSecret), config.authTokenLifetime.toSeconds)
  }
}
