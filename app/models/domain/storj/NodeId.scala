package models.domain.storj

import play.api.libs.json._

case class NodeId(nodeId: String) extends AnyVal

object NodeId {
  implicit val nodeIdFormat: OFormat[NodeId] = Json.format[NodeId]
}