package utils

import java.io.InputStream
import java.nio.ByteBuffer

import models._
import com.google.inject.{Inject, Singleton}
import play.api.Configuration
import play.Logger
import java.sql.{Connection, DriverManager, ResultSet}
import java.util.UUID

import org.apache.commons.io.IOUtils

import scala.concurrent.Future
import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormatter, ISODateTimeFormat}

import scala.concurrent.ExecutionContext.Implicits.global
import org.joda.time.format.DateTimeFormat

/**
  * Created by localhome on 10/04/2017.
  */
@Singleton
class CdsLogDatabase @Inject() (configuration: Configuration) {
  private final val logger = Logger.of(this.getClass)

  def getConnection:Option[Connection] =
    try {
      Some(DriverManager.getConnection(
        configuration.getString("JDBC_URL").get,
        configuration.getString("DB_User").get,
        configuration.getString("DB_Password").get
      )
      )
    } catch {
      case e:Exception=>{
        logger.error(e.getMessage)
        None
      }
    }

  def touuid(a: Array[Byte]): String =
  {
    val bb   = ByteBuffer.wrap(a)
    val high = bb.getLong()
    val low  = bb.getLong()
    new UUID(high, low).toString
  }

  def uuidFromStream(strm: InputStream) = touuid(IOUtils.toByteArray(strm))

  def optionalString(maybeNull:String): Option[String] = maybeNull match {
    case null=>None
    case string:String=>Some(string)
  }

  def liftJobFiles(connection: Connection, jobId: Integer, limit: Option[Integer]):Future[List[String]] = Future {
    val stmt = connection.createStatement()

    val limitPart = limit match {
      case Some(value)=>s" limit $value"
      case None=>""
    }

    val resultSet = stmt.executeQuery(s"select * from jobfiles where jobid=$jobId order by filename asc $limitPart")

    def iterateResultList(resultSet: ResultSet, accumulatingList: List[String]):List[String] = {
      if(!resultSet.next()) return accumulatingList

      iterateResultList(resultSet,
        resultSet.getString("filename") :: accumulatingList
      )
    }

    iterateResultList(resultSet, List())
  }

  def liftMetaRecords(connection: Connection, jobId: Integer, limit: Option[Integer]):Future[Map[String,String]] = Future {
    val stmt = connection.createStatement()

    val limitPart = limit match {
      case Some(value)=>s" limit $value"
      case None=>""
    }

    val resultSet = stmt.executeQuery(s"select * from jobmeta where jobid=$jobId order by identifier asc $limitPart")

    def iterateResultList(resultSet: ResultSet, accumulatingList:Map[String,String]):Map[String,String] = {
      if(!resultSet.next()) return accumulatingList

      iterateResultList(resultSet,
        Map(
          resultSet.getString("identifier")->resultSet.getString("value")
        ) ++ accumulatingList
      )
    }

    iterateResultList(resultSet,Map())
  }

  def liftJobRecords(connection:Connection, limit:Integer):Future[List[CdsJob]] = Future {
    val stmt = connection.createStatement()
    val resultSet = stmt.executeQuery(s"select * from jobs order by created desc limit $limit")

    logger.debug(resultSet.toString)

    def iterateResultList(resultSet:ResultSet,accumulatingList:List[CdsJob]):List[CdsJob] = {
      if(!resultSet.next()) return accumulatingList
      val  parser:DateTimeFormatter   = DateTimeFormat.forPattern("YYYY-MM-DD HH:mm:ss.S")
      val length = accumulatingList.length

      logger.debug(s"Iteration $length: $resultSet")
      logger.debug(accumulatingList.toString)
      //now .next() has been called, ResultSet should be updated...
      iterateResultList(resultSet,
        CdsJob(
          resultSet.getInt(1),
          uuidFromStream(resultSet.getBinaryStream(2)),
          parser.parseDateTime(resultSet.getString(3)), //DateTime
          resultSet.getString(4),
          optionalString(resultSet.getString(5)),
          optionalString(resultSet.getString(6)),
          optionalString(resultSet.getString(7))
        ) :: accumulatingList
      )
    }

    val rtn = iterateResultList(resultSet,List()).reverse
    logger.error(s"Final result: $rtn")
    rtn
  }

  def listJobs(limit:Integer):Option[Future[List[CdsJob]]] = {
    getConnection match {
      case Some(connection)=>
        Some(liftJobRecords(connection,limit))
      case None=>None
    }
  }

  def getMeta(jobId:Integer,limit:Option[Integer]):Option[Future[Map[String,String]]] = {
    getConnection match {
      case Some(connection)=>
        Some(liftMetaRecords(connection, jobId, limit))
      case None=>None
    }
  }

  def getFiles(jobId: Integer, limit:Option[Integer]):Option[Future[List[String]]] = {
    getConnection match {
      case Some(connection)=>
        Some(liftJobFiles(connection,jobId,limit))
      case None=>None
    }
  }
}
