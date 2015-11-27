package ckit

import models.Job

object GridEngine extends GridEngine

trait GridEngine {

  def jobs: Seq[Job] = {
    import sys.process._
    val output: String = "qstat -xml -u *".!!
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

}
