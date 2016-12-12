package models.domain.storj

import play.api.libs.json._

case class Email(email: String) extends AnyVal

object Email {
  implicit val emailFormat: OFormat[Email] = Json.format[Email]
}