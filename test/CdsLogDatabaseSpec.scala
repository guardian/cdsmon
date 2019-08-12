import com.google.inject.Inject
import models.CdsJobStatus
import org.joda.time.DateTime
import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._
import play.api.test.{WithApplication, WithBrowser}
import utils.CdsLogDatabase
import org.specs2.mock.Mockito
import play.api.{Application, Configuration}
import java.util.UUID

import models.LogEntry
import org.specs2.execute.{AsResult, ResultExecution, Success}
import org.specs2.concurrent.ExecutionEnv

import scala.concurrent.Future
import scala.concurrent.duration._

@RunWith(classOf[JUnitRunner])
class CdsLogDatabaseSpec (implicit ee: ExecutionEnv) extends Specification {
  // allows us to use Unit returns in test cases
//  implicit def unitAsResult: AsResult[Unit] = new AsResult[Unit] {
//    def asResult(r: =>Unit) =
//      ResultExecution.execute(r)(_ => Success())
//  }

  "CdsLogDatabase" should {
    "lift log content" in new WithApplication {
      val db = new CdsLogDatabase(app.configuration)
      val testid = "cc0f6235-2b5a-4322-af29-bd66c12ec96b"

      val resultFuture = db.liftLogContent(db.getConnection.get, UUID.fromString(testid), 0,100)
      resultFuture map { result=>
        println(result)
        result must have size 100
        result.head.message must be equalTo "INFO: Setting up datastore in /var/spool/cds_backend/OggEncoder_xml/cds_OggEncoder_xml_2017-06-07_16_25_01\n"
        result.head.methodName must be equalTo "Datastore"
        result.head.id must be equalTo 604550622
        result.head.status must be equalTo 4
        result.head.timeStamp must be equalTo DateTime.parse("2017-06-07T16:25:01.000+01:00")

      } await(1,2.seconds)
    }

    "lift job status" in new WithApplication {
      val db = new CdsLogDatabase(app.configuration)
      val testid = "cc0f6235-2b5a-4322-af29-bd66c12ec96b"

      val resultFuture = db.liftJobStatus(db.getConnection.get, testid, None)
      resultFuture.map { result=>
        val content = result.get

        content.routeStatus must be equalTo "something"
      } await(1,2.seconds)
    }
  }
}
