package models.domain.storj

import play.api.libs.json._

case class Contact(id: String,
                   protocol: String,
                   userAgent: String,
                   address: String,
                   port: Int,
                   lastSeen: String)

object Contact {
  implicit val contactFormat = Json.format[Contact]
}
