package controllers

import play.api._
import play.api.mvc._

import ckit._
import models._

object Application extends Controller {

  def index = Action {
    val name = sys.env get "SGE_CLUSTER_NAME"
    Ok(views.html.index(name))
  }

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
      case Some(j: RunningJobInfo) =>
        Ok(views.html.JobDetailRunning(j))
      case Some(j: WaitingJobInfo) =>
        Ok(views.html.JobDetailWaiting(j))
      case Some(j: AccountingJobInfo) =>
        Ok(views.html.JobDetailAccounting(j))
      case None =>
        Ok(views.html.UnknownJob(id))
    }
  }

}
