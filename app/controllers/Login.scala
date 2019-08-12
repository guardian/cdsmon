package controllers

import javax.inject.{Inject, Singleton}
import play.api.mvc._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class Login @Inject() (cc:ControllerComponents) extends AbstractController(cc)  {

  def logout = Action.async { implicit request =>
    Future(Ok(""))
  }

  def loginStatus = Action { request =>
    //val user = request.user
    Ok(views.html.loginStatus("test"))
  }
}