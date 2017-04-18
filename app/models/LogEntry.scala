package models
import org.joda.time.DateTime

/**
  * Created by localhome on 18/04/2017.
  */
case class LogEntry (id: Int, status:Int,methodName: String, message:String, timeStamp: DateTime){

}
