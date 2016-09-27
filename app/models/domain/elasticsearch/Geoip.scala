package models.domain.elasticsearch

import play.api.libs.json._

case class Geoip(ip: String,
                 countryCode2: String,
                 countryCode3: String,
                 countryName: Option[String],
                 continentCode: Option[String],
                 regionName: Option[String],
                 cityName: Option[String],
                 postalCode: Option[String],
                 latitude: Option[Double],
                 longitude: Option[Double],
                 dmaCode: Option[Int],
                 areaCode: Option[Int],
                 timeZone: String,
                 realRegionName: Option[String]) {
  def location: (Double, Double) = (latitude.getOrElse(0.0), longitude.getOrElse(0.0))
}

object Geoip {
  implicit val geoipReads = new Reads[Geoip] {
    def reads(json: JsValue): JsResult[Geoip] = json match {
      case obj: JsObject => try {
        val ip = (obj \ "ip").as[String]
        val countryCode2 = (obj \ "country_code2").as[String]
        val countryCode3 = (obj \ "country_code3").as[String]
        val countryName = (obj \ "country_name").asOpt[String]
        val continentCode = (obj \ "continent_code").asOpt[String]
        val regionName = (obj \ "region_name").asOpt[String]
        val cityName = (obj \ "city_name").asOpt[String]
        val postalCode = (obj \ "postal_code").asOpt[String]
        val latitude = (obj \ "latitude").asOpt[Double]
        val longitude = (obj \ "longitude").asOpt[Double]
        val dmaCode = (obj \ "dma_code").asOpt[Int]
        val areaCode = (obj \ "area_code").asOpt[Int]
        val timeZone = (obj \ "timezone").as[String]
        val realRegionName = (obj \ "real_region_name").asOpt[String]

        JsSuccess(Geoip(ip,
          countryCode2,
          countryCode3,
          countryName,
          continentCode,
          regionName,
          cityName,
          postalCode,
          latitude,
          longitude,
          dmaCode,
          areaCode,
          timeZone,
          realRegionName))
      } catch {
        case err: Throwable => JsError(err.getMessage)
      }
    }
  }
  implicit val geoipWrites = Json.writes[Geoip]
}
