package models.domain.storj

import play.api.libs.json._

case class Frame(id: String,
                 user: Email,
                 shards: Vector[Pointer],
                 size: Int,
                 locked: Boolean,
                 created: String)

object Frame {
  implicit val frameFormat: OFormat[Frame] = Json.format[Frame]
}
