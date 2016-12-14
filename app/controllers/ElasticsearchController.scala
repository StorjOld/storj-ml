package controllers

import com.typesafe.config.{Config, ConfigFactory}
import javax.inject.Inject
import java.io._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws._
import play.api.mvc.{Action, AnyContent, Controller}
import utils.{ElasticSearchConfig, QueryBuilder}

class ElasticsearchController @Inject()(ws: WSClient) extends Controller {

  val esConfig: Config = ConfigFactory.load().getConfig("elasticsearch.data-api")
  val esUrl: String = esConfig.getString("host")
  val esPort: Int = esConfig.getInt("port")
  val esIndex: String = esConfig.getString("index")
  val esApi = "_search"

  val esConfigString: String = ElasticSearchConfig(esUrl, esPort, esIndex, esApi).build

  val errorQueryParameters: String = QueryBuilder(10000,
    true, "fake").build
  val errorQueryUrl: String = esConfigString + errorQueryParameters
  val errorFutureRes: WSRequest = ws.url(errorQueryUrl)

  def getErrorEsData(filePath: String): Action[AnyContent] = Action.async{
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
}