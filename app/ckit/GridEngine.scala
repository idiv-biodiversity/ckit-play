package ckit

import models._

object GridEngine extends GridEngine

trait GridEngine {

  def jobs(user: Option[String]): Seq[Job] = {
    import sys.process._

    val output: String = user match {
      case Some(user) =>
        s"qstat -xml -u $user".!!
      case None =>
        "qstat -xml -u *".!!
    }

    val xml = scala.xml.XML.loadString(output)

    for {
      job <- xml \\ "job_list"
      id = (job \ "JB_job_number").text.toLong
      priority = (job \ "JAT_prio").text.toDouble
      name = (job \ "JB_name").text
      owner = (job \ "JB_owner").text
      state = (job \ "state").text
      start = (job \ "JAT_start_time").text
      queue = (job \ "queue_name").text
      slots = (job \ "slots").text.toInt
    } yield Job(id, priority, name, owner, state, start, queue, slots)
  }

  def job(id: Long): JobInfo = {
    import sys.process._
    val output: String = s"qstat -xml -j $id".!!
    val xml = scala.xml.XML.loadString(output)

    val messages = for {
      message <- xml \\ "MES_message"
    } yield message.text

    JobInfo(id, messages)
  }

}
