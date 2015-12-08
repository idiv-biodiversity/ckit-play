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

  def isRunning(xml: scala.xml.Elem): Boolean = {
    (xml \\ "JAT_start_time").size > 0
  }

  def runningJob(id: Long, xml: scala.xml.Elem): RunningJobInfo = {
    val usages = for {
      task <- xml \\ "JB_ja_tasks" \ "element"
      taskid = (task \ "JAT_task_number").text.toLong
      wallclock = task \\ "JAT_scaled_usage_list" \\ "Events" find {
        el => (el \ "UA_name").text == "wallclock"
      } map {
        el => (el \ "UA_value").text
      } getOrElse ""
      cpu = task \\ "JAT_scaled_usage_list" \\ "Events" find {
        el => (el \ "UA_name").text == "cpu"
      } map {
        el => (el \ "UA_value").text
      } getOrElse ""
      maxvmem = task \\ "JAT_scaled_usage_list" \\ "Events" find {
        el => (el \ "UA_name").text == "maxvmem"
      } map {
        el => (el \ "UA_value").text
      } getOrElse ""
    } yield taskid -> Usage(wallclock, cpu, maxvmem)

    RunningJobInfo(id, usages.sortBy(_._1).toList)
  }

  def waitingJob(id: Long, xml: scala.xml.Elem): WaitingJobInfo = {
    var messages = for {
      message <- xml \\ "MES_message"
    } yield message.text

    val resources = for {
      el <- xml \\ "JB_hard_resource_list" \ "element"
      name = (el \ "CE_name").text
      value = (el \ "CE_stringval").text
    } yield (name,value)

    val reserve = (xml \\ "JB_reserve").text match {
      case "" | "false" => false
      case "true" => true
    }

    val script = (xml \\ "JB_script_file").text match {
      case s @ "STDIN" => s
      case s =>
        val path = xml \\ "JB_env_list" \\ "element" find {
          el => (el \ "VA_variable").text == "__SGE_PREFIX__O_WORKDIR"
        } map {
          el => (el \ "VA_value").text
        }

        path.fold(ifEmpty = s) {
          _ + sys.props("file.separator") + s
        }
    }

    val pe = (xml \\ "JB_pe").text match {
      case "" => None
      case name =>
        val range = xml \\ "JB_pe_range"
        val min = (range \\ "RN_min").text.toInt
        val max = (range \\ "RN_max").text.toInt
        val step = (range \\ "RN_step").text.toInt
        Some(PE(name, min, max, step))
    }

    // if scheduler messages have been turned off
    if (messages.size == 1 &&
        messages.head == "(Collecting of scheduler job information is turned off)") {
      messages = messagesFromQalter(id)
    }

    WaitingJobInfo(id, resources.toMap, pe, reserve, script, messages)
  }

  def messagesFromQalter(id: Long): List[String] = {
    import sys.process._
    s"qalter -w p $id".lineStream_!.toList
  }

  def job(id: Long): JobInfo = {
    import sys.process._
    val output: String = s"qstat -xml -j $id".!!
    val xml = scala.xml.XML.loadString(output)

    if (isRunning(xml))
      runningJob(id, xml)
    else
      waitingJob(id, xml)
  }

}
