package controllers

import play.api.Configuration
import play.api.mvc._
import javax.inject.{Inject,Singleton}
import models.LogEntry
import org.joda.time.DateTime
import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import org.joda.time.format.ISODateTimeFormat
import play.Logger
import utils.CdsLogDatabase
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import java.util.UUID
import utils.CdsLogDatabase

/**
  * Created by localhome on 18/04/2017.
  */
@Singleton
class LogApi @Inject() (configuration: Configuration, db:CdsLogDatabase,cc:ControllerComponents) extends AbstractController(cc) {
  private final val logger = Logger.of(this.getClass)

  implicit val LogEntryEncoder:Encoder[LogEntry] = Encoder.instance {
    case LogEntry(internalId,status,methodName,message,timeStamp) => Json.obj(
      "pk" -> internalId.asJson,
      "status" -> status.asJson,
      "methodName" -> methodName.asJson,
      "message" -> message.asJson,
      "timestamp" -> ISODateTimeFormat.dateTimeNoMillis().print(timeStamp).asJson
    )
  }

  def validateUuid(uuidString:String):Option[UUID] = {
    try {
      Some(UUID.fromString(uuidString))
    } catch {
      case e:java.lang.IllegalArgumentException=>None
    }
  }

  def logof(externalid: String) = Action.async { request=>
    val params = request.queryString.map{ case (k,v)=> k->v.mkString }
    val startAt:Int = params.getOrElse("startAt","0").toInt
    val pageSize:Int = params.getOrElse("size","10").toInt

    validateUuid(externalid) match {
      case Some(uuid)=>
        db.getLog(uuid, startAt, pageSize) match {
          case Some(future)=>future.map((logList)=>Ok(logList.asJson.noSpaces).withHeaders("Content-Type"->"application/json"))
          case None=>Future(InternalServerError("Could not access database"))
        }
      case None=>Future(BadRequest("Invalid UUID"))
    }
  }
}
