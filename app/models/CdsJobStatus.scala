package models

/**
  * Created by localhome on 10/04/2017.
  */
case class CdsJobStatus (externalId:String, lastOperation:Option[String], lastOperationStatus:Option[String],
                         currentOperation:Option[String], routeStatus:String, lastError:Option[String])
{

}
