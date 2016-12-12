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
        val id = (obj \ "free").as[String]
        val dataHash = (obj \ "used").as[String]
        val reporterId = (obj \ "upload").as[String]
        val farmerId = (obj \ "download").as[String]
        val clientId = (obj \ "userAgent").as[String]
        val exchangeStart = (obj \ "protocol").as[String]
        val exchangeEnd = (obj \ "address").as[String]
        val exchangeResultCode = (obj \ "port").as[Int]
        val exchangeResultMessage = (obj \ "nodeId").as[String]
        val created = (obj \ "lastSeen").as[String]

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
  implicit val ExchangeReportWrites = Json.writes[ExchangeReport]
}
