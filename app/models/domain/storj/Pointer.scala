package models.domain.storj

import play.api.libs.json._

case class Pointer(id: String,
                   hash: String,
                   size: Int,
                   index: Int,
                   tree: Vector[String],
                   challenges: Vector[String])

object Pointer {
  implicit val pointerFormat: OFormat[Pointer] = Json.format[Pointer]
}