package models.domain.storj

import play.api.libs.json._

case class Shard(id: String,
                 hash: String,
                 contracts: Vector[(NodeId, Contract)],
                 trees: Vector[(NodeId, Vector[String])],
                 challenges: Vector[(NodeId, Challenge)],
                 meta: Vector[(NodeId, Vector[String])])


object Shard {
}
