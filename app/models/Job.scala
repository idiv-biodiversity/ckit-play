package models

import play.api.libs.json.Json

case class Job(id: Long, name: String)

object Job {
  implicit val jobFormat = Json.format[Job]
}
