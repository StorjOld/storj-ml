package controllers

import java.io._
import javax.inject.Inject

import com.typesafe.config.ConfigFactory
import models.domain.storj.Geoip
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import play.api.libs.ws._
import play.api.mvc.{Action, Controller}
import utils.{ElasticSearchConfig, QueryBuilder}

class ElasticSearchController @Inject()(ws: WSClient) extends Controller {

  val esConfig = ConfigFactory.load().getConfig("elasticsearch.data-api")
  val esUrl = esConfig.getString("host")
  val esPort = esConfig.getInt("port")
  val esIndex = esConfig.getString("index") + "2016.09.22"
  val esApi = "_search"

  val esConfigString = ElasticSearchConfig(esUrl, esPort, esIndex, esApi).build

  val geoQueryParameters = QueryBuilder(1000,
    true,
    "verb:GET",
    "request:/buckets/*",
    "request:/*/files",
    "request:skip",
    "!request:skip=0",
    "request:exclude").build
  val geoQueryUrl =  esConfigString + geoQueryParameters
  val geoFutureRes = ws.url(geoQueryUrl)

  val errorQueryParameters = QueryBuilder(10000,
    true,
    "app.raw:bridge",
    "type.raw:nodejs-access" ,
    "environment.raw:production",
    "message:dropping").build
  val errorQueryUrl = esConfigString + errorQueryParameters
  val errorFutureRes = ws.url(errorQueryUrl)

  def getErrorEsData(filePath: String) = Action.async{
    val dataFile = new PrintWriter(new File(filePath))
    val returnData = errorFutureRes.get
    returnData.map{
      docs => {
          dataFile.write(docs.body)
          dataFile.close()
        Ok("Error Data Saved at " + filePath)
      }
    }
  }

  def getGeoData(filePath: String) = Action.async{
    val dataFile = new PrintWriter(new File(filePath))
    val returnData = geoFutureRes.get
    returnData.map{
      docs => {
        dataFile.write(docs.body)
        dataFile.close()
        Ok("Geo Data Saved at " + filePath)
      }
    }
  }

  def getGeoCounts = Action.async{
    geoFutureRes.get.map { geo =>
      val geoParsed = Json.parse(geo.body) \\ "geoip"
      val geoValidated = geoParsed.map{geo =>
       geo.validate[Geoip].asOpt
      }
      Ok((for {
        geo <- geoValidated
        data <- geo
        country <- data.countryName
      } yield {
        country
      }).groupBy(x => x)
          .mapValues(_.size)
        .toString())
    }
  }
}
