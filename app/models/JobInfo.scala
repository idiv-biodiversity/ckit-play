package models

import play.api.libs.json.Json

sealed abstract class JobInfo {
  def id: Long
}

case class RunningJobInfo(
  id: Long,
  usages: Seq[(Long,Usage)]
) extends JobInfo

case class Usage(wallclock: String, cpu: String, maxvmem: String)

case class WaitingJobInfo(
  id: Long,
  resources: Map[String,String],
  pe: Option[PE],
  reserve: Boolean,
  script: String,
  messages: Seq[String]
) extends JobInfo

case class PE(name: String, min: Int, max: Int, step: Int)
