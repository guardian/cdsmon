import java.time.ZonedDateTime

import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._
import play.api.test.Helpers._
import play.api.test._
import models.LogEntry
import org.joda.time.DateTime

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
@RunWith(classOf[JUnitRunner])
class LogEntrySpec extends Specification {

  "LogEntry" should {
    "exist" in new WithBrowser {
      val entry = LogEntry(0,0,"methodName","message",ZonedDateTime.now())
    }
  }
}
