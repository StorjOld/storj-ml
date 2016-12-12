package models.domain.elasticsearch

import play.api.libs.json._

case class TelemetryReport(free: Int,
                           used: Int,
                           upload: Float,
                           download: Float,
                           userAgent: String,
                           protocol: String,
                           address: String,
                           port: Int,
                           nodeId: String,
                           lastSeen: Int,
                           payment: String,
                           timestamp: String)

object TelemetryReport {
  implicit val telemetryReportReads = new Reads[TelemetryReport] {
    def reads(json: JsValue): JsResult[TelemetryReport] = json match {
      case obj: JsObject => try {
        val free = (obj \ "free").as[Int]
        val used = (obj \ "used").as[Int]
        val upload = (obj \ "upload").as[Float]
        val download = (obj \ "download").as[Float]
        val userAgent = (obj \ "userAgent").as[String]
        val protocol = (obj \ "protocol").as[String]
        val address = (obj \ "address").as[String]
        val port = (obj \ "port").as[Int]
        val nodeId = (obj \ "nodeId").as[String]
        val lastSeen = (obj \ "lastSeen").as[Int]
        val payment = (obj \ "payment").as[String]
        val timestamp = (obj \ "timestamp").as[String]

        JsSuccess(TelemetryReport(free,
          used,
          upload,
          download,
          userAgent,
          protocol,
          address,
          port,
          nodeId,
          lastSeen,
          payment,
          timestamp))
    } catch {
        case err: Throwable => JsError(err.getMessage)
      }
    }
  }
  implicit val telemetryReportWrites = Json.writes[TelemetryReport]
}
