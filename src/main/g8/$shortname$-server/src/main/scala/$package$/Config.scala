package $package$

import java.io.File
import java.util.concurrent.TimeUnit
import com.typesafe.config.{ ConfigException, ConfigFactory, Config => RawConfig }
import scala.concurrent.duration.FiniteDuration

case class Config(
  httpInterface: String,
  httpPort: Int,
  httpWebDir: Option[File])

object Config {
  def load(): Config = {
    val raw = ConfigFactory.load().getConfig("$shortname$")

    Config(
      httpInterface = raw.getString("http.interface"),
      httpPort = raw.getInt("http.port"),
      httpWebDir = raw.getOptionalString("http.web-dir").map(new File(_))
    )
  }

  implicit class RichConfig(val underlying: RawConfig) extends AnyVal {
    def getOptionalString(path: String): Option[String] = try {
      Some(underlying.getString(path))
    } catch {
      case e: ConfigException.Missing ⇒ None
    }
  }
}
