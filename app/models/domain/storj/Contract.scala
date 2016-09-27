package models.domain.storj

import play.api.libs.json._

case class Contract(version: Int,
                    storeEnd: Int,
                    storeBegin: Int,
                    renterSignature: String,
                    renterId: String,
                    paymentStoragePrice: Int,
                    paymentDownloadPrice: Int,
                    paymentDestination: String,
                    farmerSignature: String,
                    farmerId: String,
                    dataSize: Int,
                    dataHash: String,
                    auditCount: Int)

object Contract {
  implicit val contractFormat = Json.format[Contract]
}
