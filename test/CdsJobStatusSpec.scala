import models.CdsJobStatus
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
class CdsJobStatusSpec extends Specification {

  "CdsJobStatus" should {
    "exist" in new WithBrowser {
      val entry = CdsJobStatus("4FF06024-49EA-46C7-81E0-AAE0D8236F8E", Some("lastOperation"),Some("Success"),Some("currentOperation"),"Processing",None)
    }
  }
}
