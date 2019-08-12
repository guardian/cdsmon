package utils

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, InputStream}
import java.nio.ByteBuffer

import models._
import javax.inject.{Inject, Singleton}
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
      case e:Exception=>
        logger.error(e.getMessage)
        None
    }

  def touuid(a: Array[Byte]): String =
  {
    val bb   = ByteBuffer.wrap(a)
    val high = bb.getLong()
    val low  = bb.getLong()
    new UUID(high, low).toString
  }

  def uuidFromStream(strm: InputStream) = touuid(IOUtils.toByteArray(strm))

  def fromuuid(a: UUID):Array[Byte] = {
    val bb = ByteBuffer.allocate(128)
    val high = a.getMostSignificantBits
    val low= a.getLeastSignificantBits
    bb.putLong(low)
    bb.putLong(high)
    bb.array()
  }

  def uuidToStream(a:UUID) = {
    new ByteArrayInputStream(fromuuid(a))
  }

  def optionalString(maybeNull:String): Option[String] = maybeNull match {
    case null=>None
    case string:String=>Some(string)
  }

  def liftJobStatus(connection:Connection, jobId: String, limit: Option[Integer]):Future[Option[CdsJobStatus]] = Future {
    val stmt = connection.createStatement()

    val limitPart = limit match {
      case Some(value)=>s" limit $value"
      case None=>""
    }

    val resultSet = stmt.executeQuery(s"select * from jobstatus where job_externalid=bin_from_uuid('$jobId') $limitPart")

    resultSet.next() match {
      case false=>None
      case true=>
        Some(CdsJobStatus(
          uuidFromStream(resultSet.getBinaryStream(2)),
          optionalString(resultSet.getString(3)),
          optionalString(resultSet.getString(4)),
          optionalString(resultSet.getString(5)),
          resultSet.getString(6),
          optionalString(resultSet.getString(7))
        ))
    }
  }

  def liftJobFiles(connection: Connection, jobId: Integer, limit: Option[Integer]) =
    liftStringList(connection,s"select * from jobfiles where jobid=$jobId order by filename asc", "filename", limit)

  def liftRouteNames(connection: Connection, limit: Option[Integer]) =
    liftStringList(connection,s"select distinct routename from jobs", "routename", limit)

  def liftStringList(connection: Connection, sql: String, dbKey: String, limit: Option[Integer]):Future[List[String]] = Future {
    val stmt = connection.createStatement()

    val limitPart = limit match {
      case Some(value)=>s" limit $value"
      case None=>""
    }

    val resultSet = stmt.executeQuery(sql + limitPart)

    def iterateResultList(resultSet: ResultSet, accumulatingList: List[String]):List[String] = {
      if(!resultSet.next()) return accumulatingList

      iterateResultList(resultSet,
        resultSet.getString(dbKey) :: accumulatingList
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

  def liftLogContent(connection: Connection, externalId: UUID,startAt: Int, pageSize: Int) = Future {
    val stmt = connection.prepareStatement(s"select * from log where externalid=bin_from_uuid(?) limit ? offset ?")
    //stmt.setBlob(1,uuidToStream(externalId))
    stmt.setString(1,externalId.toString)
    stmt.setInt(2, pageSize)
    stmt.setInt(3, startAt)

    val resultSet = stmt.executeQuery()
    def iterateResultList(resultSet: ResultSet,accumulatingList:List[LogEntry]):List[LogEntry] = {
      if(!resultSet.next()) return accumulatingList
      val parser:DateTimeFormatter = DateTimeFormat.forPattern("YYYY-MM-dd HH:mm:ss.S")

      logger.error(resultSet.getString(4))
      iterateResultList(resultSet,
        LogEntry(
          resultSet.getInt(1),
          resultSet.getInt(3),
          resultSet.getString(4),
          resultSet.getString(5),
          parser.parseDateTime(resultSet.getString(6))
        ) :: accumulatingList
      )
    }

    iterateResultList(resultSet, List()).reverse
  }

  def liftJobRecords(connection:Connection, limit:Integer, routeFilter:Option[String],statusFilter:Option[String]):Future[List[CdsJob]] = Future {
    val stmt = connection.createStatement()
    val whereClausePieces:List[String] = List(routeFilter.map((n)=>{s"routename REGEXP \'/$n-{0,1}[0-9]*\'"}),
                                              statusFilter.map((s)=>{s"route_status=\'${s.toLowerCase}\'"})
                                             ).filter(_.isDefined).map(_.get)

    val whereClause = if(whereClausePieces.nonEmpty){
      "where " + whereClausePieces.mkString(" and ")
    } else {
      ""
    }

    val fields = List("internalid","externalid","created","routename","status","hostname","hostip")
    val fieldlist = fields.mkString(",")
    val resultSet = stmt.executeQuery(s"select $fieldlist from jobs left join jobstatus on externalid=job_externalid $whereClause group by internalid order by created desc limit $limit")

    logger.debug(resultSet.toString)

    def iterateResultList(resultSet:ResultSet,accumulatingList:List[CdsJob]):List[CdsJob] = {
      if(!resultSet.next()) return accumulatingList
      val  parser:DateTimeFormatter   = DateTimeFormat.forPattern("YYYY-MM-dd HH:mm:ss.S")
      val length = accumulatingList.length

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
    rtn
  }

  def listJobs(limit:Integer,routeFilter:Option[String],statusFilter:Option[String]):Option[Future[List[CdsJob]]] = {
    getConnection match {
      case Some(connection)=>
        Some(liftJobRecords(connection,limit,routeFilter,statusFilter))
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

  def getStatus(externalId: String, limit: Option[Integer]):Option[Future[Option[CdsJobStatus]]] = {
    getConnection match {
      case Some(connection)=>
        Some(liftJobStatus(connection,externalId,limit))
      case None=>
        None
    }
  }

  def getRouteList(limit: Option[Integer]):Option[Future[List[String]]] = {
    getConnection match {
      case Some(connection)=> Some(liftRouteNames(connection, limit))
      case None=>None
    }
  }

  def getLog(externalId:UUID,startAt:Int, pageSize:Int):Option[Future[List[LogEntry]]] = {
    getConnection match {
      case Some(connection)=>Some(liftLogContent(connection, externalId,startAt,pageSize))
      case None=>None
    }
  }
}
