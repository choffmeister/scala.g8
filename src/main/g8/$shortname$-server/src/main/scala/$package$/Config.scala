package $package$

import java.io.File
import java.util.concurrent.TimeUnit

import com.typesafe.config.{ ConfigException, ConfigFactory, Config ⇒ RawConfig }

import scala.collection.JavaConverters._
import scala.concurrent.duration.FiniteDuration

case class Config(
  httpInterface: String,
  httpPort: Int,
  httpWebDir: Option[File],
  authRealm: String,
  authUsers: Map[String, String],
  authPasswordsStorageDefaultAlgorithm: String,
  authPasswordsStorageDefaultAlgorithmConfig: List[String],
  authPasswordsValidationDelay: FiniteDuration,
  authTokenSecret: Array[Byte],
  authTokenLifetime: FiniteDuration)

object Config {
  def load(): Config = {
    val raw = ConfigFactory.load().getConfig("$shortname$")

    Config(
      httpInterface = raw.getString("http.interface"),
      httpPort = raw.getInt("http.port"),
      httpWebDir = raw.getOptionalString("http.web-dir").map(new File(_)),
      authRealm = raw.getString("auth.realm"),
      authUsers = raw.getStringMap("auth.users"),
      authPasswordsStorageDefaultAlgorithm = raw.getString("auth.passwords.storage.default-algorithm").split(":", -1).head,
      authPasswordsStorageDefaultAlgorithmConfig = raw.getString("auth.passwords.storage.default-algorithm").split(":", -1).tail.toList,
      authPasswordsValidationDelay = raw.getFiniteDuration("auth.passwords.validation.delay"),
      authTokenSecret = raw.getString("auth.token.secret").getBytes("ASCII"),
      authTokenLifetime = raw.getFiniteDuration("auth.token.lifetime")
    )
  }

  implicit class RichConfig(val underlying: RawConfig) extends AnyVal {
    def getOptionalString(path: String): Option[String] = try {
      Some(underlying.getString(path))
    } catch {
      case e: ConfigException.Missing ⇒ None
    }

    def getFiniteDuration(path: String): FiniteDuration = {
      val unit = TimeUnit.MICROSECONDS
      FiniteDuration(underlying.getDuration(path, unit), unit)
    }

    def getStringMap(path: String): Map[String, String] = {
      underlying.getConfig(path).root.asScala.toMap.map(e ⇒ e._1 -> e._2.unwrapped.asInstanceOf[String])
    }
  }
}
