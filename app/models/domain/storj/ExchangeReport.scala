package models.domain.storj

import play.api.libs.json._

case class ExchangeReport(id: String,
                          dataHash: String,
                          reporterId: String,
                          farmerId: String,
                          clientId: String,
                          exchangeStart: String,
                          exchangeEnd: String,
                          exchangeResultCode: Int,
                          exchangeResultMessage: String,
                          created: String) 

object ExchangeReport {
  implicit val ExchangeReportReads = new Reads[ExchangeReport] {
    def reads(json: JsValue): JsResult[ExchangeReport] = json match {
      case obj: JsObject => try {
        val id = (obj \ "_id").as[String]
        val dataHash = (obj \ "dataHash").as[String]
        val reporterId = (obj \ "reporterId").as[String]
        val farmerId = (obj \ "farmerId").as[String]
        val clientId = (obj \ "clientId").as[String]
        val exchangeStart = (obj \ "exchangeStart").as[String]
        val exchangeEnd = (obj \ "exchangeEnd").as[String]
        val exchangeResultCode = (obj \ "exchangeResultCode").as[Int]
        val exchangeResultMessage = (obj \ "exchangeResultMessage").as[String]
        val created = (obj \ "created").as[String]

        JsSuccess(ExchangeReport(id,
          dataHash,
          reporterId,
          farmerId,
          clientId,
          exchangeStart,
          exchangeEnd,
          exchangeResultCode,
          exchangeResultMessage,
          created))
      } catch {
        case err: Throwable => JsError(err.getMessage)
      }
    }
  }
  implicit val ExchangeReportWrites: OWrites[ExchangeReport] = Json.writes[ExchangeReport]
}
