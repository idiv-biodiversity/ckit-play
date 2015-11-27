package controllers

import play.api._
import play.api.mvc._

import ckit._
import models.Job

object Application extends Controller {

  def index = TODO

  def jobs = Action {
    Ok(views.html.JobTable(GridEngine.jobs))
  }

}
