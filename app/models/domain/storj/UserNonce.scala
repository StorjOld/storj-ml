package models.domain.storj

import play.api.libs.json._

case class UserNonce(id: String, user: Email, nonce: String, timestamp: String)

object UserNonce {
  implicit val userNonceFormat: OFormat[UserNonce] = Json.format[UserNonce]
}
