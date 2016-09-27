package models.domain.storj

import play.api.libs.json._

case class Token(id: String, bucket: Bucket, operation: String, expires: String)

object Token {
  implicit val tokenFormat = Json.format[Token]
}
