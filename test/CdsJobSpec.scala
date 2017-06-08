import models.CdsJob
import org.joda.time.DateTime
import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._
import play.api.test.WithBrowser

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
@RunWith(classOf[JUnitRunner])
class CdsJobSpec extends Specification {

  "CdsJobStatus" should {
    "exist" in new WithBrowser {
      //val entry = CdsJob()
    }
  }
}
