package controllers

import javax.inject.Inject
import play.api.mvc._

class Application @Inject() (cc:ControllerComponents) extends AbstractController(cc) {
  def mainview(path:String) = Action {
    Ok(views.html.main("CDS Monitor"))
  }

  def mainview_none= Action {
    Ok(views.html.main("CDS Monitor"))
  }
}