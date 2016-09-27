package models.domain.storj

import play.api.libs.json._

case class PublicKey(id: String, user: Email, label: String)

object PublicKey {
  implicit val publicKeyFormat = Json.format[PublicKey]
}
