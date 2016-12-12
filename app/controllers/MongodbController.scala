package controllers

import com.typesafe.config.{Config, ConfigFactory}
import javax.inject.Inject

import models.domain.storj.{Email, ExchangeReport, User}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.{Action, AnyContent, Controller}
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.api.{DefaultDB, MongoConnection, MongoDriver}
import reactivemongo.api.MongoConnection.ParsedURI
import reactivemongo.bson.{BSONDocument, BSONDocumentReader, Macros}

import scala.concurrent.Future
import scala.util.Try

class MongodbController @Inject()(val reactiveMongoApi: ReactiveMongoApi) extends Controller
  with MongoController
  with ReactiveMongoComponents {

  val mongoConfig: Config = ConfigFactory.load().getConfig("mongodb")
  val mongoUri: String = mongoConfig.getString("uri")

  val mongoDriver = MongoDriver()
  val parsedUri: Try[ParsedURI] = MongoConnection.parseURI(mongoUri)
  val mConnection: Try[MongoConnection] = parsedUri.map(driver.connection(_))
  val futureConnection: Future[MongoConnection] = Future.fromTry(mConnection)
  val bridgeDb: Future[DefaultDB] = futureConnection.flatMap(_.database("bridge"))

  val usersCollection: Future[BSONCollection] = bridgeDb.map(_.collection("users"))
  val exchangeReportsCollection: Future[BSONCollection] = bridgeDb.map(_.collection("exchangereports"))

  implicit val emailReader: BSONDocumentReader[Email] = Macros.reader[Email]
  implicit val userReader: BSONDocumentReader[User] = Macros.reader[User]

  def findUserByEmail(id: String): Action[AnyContent] = Action.async{
    val findById = BSONDocument("id" -> id)
    val data = usersCollection.map{ docs =>
    docs.find(findById).cursor[User].collect[Vector]()
    }
    data.map(x => Ok(x.toString))
  }

  def loadExchangeReports(date: String): Action[AnyContent] = Action.async{
    val filterByDate = BSONDocument("created" -> date)
    val reportData = exchangeReportsCollection.map { docs =>
      docs.find(filterByDate).cursor[ExchangeReport].collect[Vector]()
    }
    reportData.map(er => Ok(er.toString))
  }
}
