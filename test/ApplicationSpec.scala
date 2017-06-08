import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import play.api.test.Helpers._

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
@RunWith(classOf[JUnitRunner])
class ApplicationSpec extends Specification {

  "Application" should {

    "render the index page" in new WithApplication{
      val home = route(FakeRequest(GET, "/")).get

      status(home) must equalTo(OK)
      contentType(home) must beSome.which(_ == "text/html")
      contentAsString(home) must contain ("<title>CDS Monitor</title>")
      contentAsString(home) must contain ("<script src=\"/assets/javascripts/bundle.js\"></script>")
    }

    "return json of route names" in new WithApplication {
      val content = route(FakeRequest(GET,"/routenames")).get

      status(content) must equalTo(OK)
      contentType(content) must beSome.which(_ == "application/json")
      println(contentAsString(content))
      contentAsString(content) must contain ("\"Interactive_Transcoding_Workflow_Route\",\"DR_site_HLS\"")
    }
  }
}
