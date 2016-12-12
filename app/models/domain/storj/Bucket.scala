package models.domain.storj

import play.api.libs.json._

case class Bucket(id: String,
                  user: Email,
                  created: String,
                  name: String,
                  pubkeys: Vector[String],
                  status: String,
                  transfer: Int,
                  storage: Int)

object Bucket {
  implicit val bucketFormat: OFormat[Bucket] = Json.format[Bucket]
}
