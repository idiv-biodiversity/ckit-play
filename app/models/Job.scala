package models

import play.api.libs.json.Json

case class Job(id: Long, priority: Double, name: String, owner: String, state: String, start: String, queue: String, slots: Int, tasks: String)

object Job {
  implicit val jobFormat = Json.format[Job]
}
