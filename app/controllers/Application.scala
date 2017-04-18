package controllers

import play.api._
import play.api.mvc._

object Application extends Controller {
  def mainview(path:String) = Action {
    Ok(views.html.main("CDS Monitor"))
  }

  def mainview_none= Action {
    Ok(views.html.main("CDS Monitor"))
  }
}