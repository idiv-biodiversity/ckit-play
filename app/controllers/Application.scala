package controllers

import play.api._
import play.api.mvc._

import ckit._
import models._

object Application extends Controller {

  def index = TODO

  def jobs = Action {
    val jobs = GridEngine.jobs(None)
    if (jobs.nonEmpty)
      Ok(views.html.JobTable(jobs))
    else
      Ok(views.html.NoJobs("No Jobs"))
  }

  def jobsOf(user: String) = Action {
    val jobs = GridEngine.jobs(Some(user))
    if (jobs.nonEmpty)
      Ok(views.html.JobTable(jobs))
    else
      Ok(views.html.NoJobs(s"No Jobs of $user"))
  }

  def job(id: Long) = Action {
    GridEngine.job(id) match {
      case j: RunningJobInfo =>
        Ok(views.html.JobDetailRunning(j))
      case j: WaitingJobInfo =>
        Ok(views.html.JobDetailWaiting(j))
    }
  }

}
