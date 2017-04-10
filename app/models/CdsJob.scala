package models

import org.joda.time.DateTime

sealed trait CdsModel {

}
/**
  * Created by localhome on 10/04/2017.
  */
case class CdsJob(internalId: Integer, externalId: String, created: DateTime,
                  routeName: String, status: Option[String], hostname: Option[String], hostip:Option[String]) extends CdsModel {

}
