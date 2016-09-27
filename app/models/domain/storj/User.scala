package models.domain.storj

import models.domain.storj.Email._
import play.api.libs.json._

case class User(id: Email,
                hashpass: String,
                pendingHashPass: Option[String],
                activated: Option[Boolean],
                activator: Option[String],
                deactivator: Option[String],
                resetter: Option[String],
                created: String
               )

object User {
  implicit object UserWrites extends Writes[User] {
    def writes(user: User): JsObject = Json.obj(
      "_id" -> user.id,
      "hashpass" -> user.hashpass,
      "pendingHashPass" -> user.pendingHashPass,
      "activated" -> user.activated,
      "activator" -> user.activator,
      "deactivator" -> user.deactivator,
      "resetter" -> user.resetter,
      "created" -> user.created
    )
  }
  implicit object UserReads extends Reads[User] {
    def reads(json: JsValue): JsResult[User] = json match {
      case obj: JsObject => try {
        val id = (obj \ "_id").as[Email]
        val hashpass = (obj \ "hashpass").as[String]
        val pendingHashPass = (obj \ "pendingHashPass").asOpt[String]
        val activated = (obj \ "activated").asOpt[Boolean]
        val activator = (obj \ "activator").asOpt[String]
        val deactivator = (obj \ "deactivator").asOpt[String]
        val resetter = (obj \ "resetter").asOpt[String]
        val created = (obj \ "created").as[String]

        JsSuccess(User(id, hashpass, pendingHashPass, activated, activator, deactivator,resetter, created))
      } catch {
        case cause: Throwable => JsError(cause.getMessage)
      }
    }
  }
}
