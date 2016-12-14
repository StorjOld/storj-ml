package models.domain.storj

import play.api.libs.json._

case class FarmerProfile(created: String, farmerId: String, failureRate: Option[Double])

object FarmerProfile {
    implicit val FarmerProfileReads = new Reads[FarmerProfile] {
      def reads(json: JsValue): JsResult[FarmerProfile] = json match {
        case obj: JsObject => try {
          val created = (obj \ "created").as[String]
          val farmerId = (obj \ "farmerId").as[String]
          val failureRate = (obj \\ "failureRate").asOpt[Double]

          JsSuccess(FarmerProfile(created, farmerId, failureRate))
        } catch {
          case err: Throwable => JsError(err.getMessage)
        }
      }
    }
    implicit val FarmerProfileWrites: OWrites[FarmerProfile] = Json.writes[FarmerProfile]
}