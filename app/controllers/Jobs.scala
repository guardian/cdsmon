package controllers

import play.api.Configuration
import play.api.mvc._
import com.google.inject.Inject
import models.{CdsJob, CdsModel}
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
/**
  * Created by localhome on 10/04/2017.
  */
class Jobs @Inject() (configuration: Configuration, db:CdsLogDatabase) extends Controller {
  //implicit val CdsJobEncoder:Encoder[CdsJob] = Encoder[CdsJob]
  private final val logger = Logger.of(this.getClass)

  implicit val CdsJobEncoder: Encoder[CdsJob] = Encoder.instance {
    case CdsJob(internalId,externalId,created,routeName,status,hostname,hostip) => Json.obj(
      "internalId"   -> internalId.toString.asJson,
      "externalId" -> externalId.asJson,
      "created"      -> ISODateTimeFormat.dateTimeNoMillis().print(created).asJson,
      "routeName"->routeName.asJson,
      "status"->status.asJson,
      "hostname"->hostname.asJson,
      "hostip"->hostip.asJson
    )
  }

  def list = Action.async {
    db.listJobs(10) match {
      case Some(joblist:Future[List[CdsJob]])=>
        joblist.map((joblist)=>{
          logger.error(joblist.toString)
          Ok(joblist.asJson.noSpaces).withHeaders("Content-Type"->"application/json")
        })
      case None=>
        Future(InternalServerError("Nothing returned from database"))
    }
  }

  def metadata(internalId:String) = Action.async {
    db.getMeta(internalId.toInt,None) match {
      case Some(meta)=>
        meta.map((metaMap)=>Ok(metaMap.asJson.noSpaces).withHeaders("Content-Type"->"application/json"))
      case None=>
        Future(InternalServerError("Nothing returned from database"))
    }
  }

  def files(internalId:String) = Action.async {
    db.getFiles(internalId.toInt,None) match {
      case Some(files)=>
        files.map((filesList)=>Ok(filesList.asJson.noSpaces).withHeaders("Content-Type"->"application/json"))
      case None=>
        Future(InternalServerError("Nothing returned from database"))
    }
  }

  def status(externalId:String) = Action.async {
    db.getStatus(externalId, None) match {
      case Some(status)=>
        status.map((status)=>Ok(status.asJson.noSpaces).withHeaders("Content-Type"->"application/json"))
      case None=>
        Future(InternalServerError("Nothing returned from database"))
    }
  }
}
