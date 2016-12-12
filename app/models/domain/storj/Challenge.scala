package models.domain.storj

import play.api.libs.json._

case class Challenge(challenges: Vector[String], depth: Int, root: String)

object Challenge {
  implicit val challengeFormat: OFormat[Challenge] = Json.format[Challenge]
}
