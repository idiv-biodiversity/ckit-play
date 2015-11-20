package ckit

import models.Job

object GridEngine {

  def qstat: Seq[Job] = {
    import sys.process._
    val output: String = "qstat -xml -u *".!!
    val xml = scala.xml.XML.loadString(output)

    for {
      job <- xml \\ "job_list"
      id = (job \ "JB_job_number").text.toLong
      name = (job \ "JB_name").text
    } yield Job(id, name)
  }

}
