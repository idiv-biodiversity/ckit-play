package models

import play.api.libs.json.Json

case class JobInfo(id: Long, messages: Seq[String])

object JobInfo {
  implicit val jobDetailFormat = Json.format[JobInfo]
}
