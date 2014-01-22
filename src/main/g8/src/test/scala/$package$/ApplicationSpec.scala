package $package$

import org.junit.runner.RunWith
import org.specs2.mutable._
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class ApplicationSpec extends Specification {
  "Application" should {
    "run" in {
      val app = new Application()
      app.run(Array.empty[String])

      ok
    }
  }
}
