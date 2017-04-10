package controllers

import play.api._
import play.api.mvc._

object Application extends Controller {
  def mainview = Action {
    Ok(views.html.main("CDS Monitor"))
  }

}