package controllers

import javax.inject.Inject

import com.typesafe.config.ConfigFactory
import models.domain.storj.{Email, User}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.{Action, Controller}
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.api.{DefaultDB, MongoConnection, MongoDriver}
import reactivemongo.bson.{BSONDocument, BSONDocumentReader, Macros}

import scala.concurrent.Future

class MongodbController @Inject()(val reactiveMongoApi: ReactiveMongoApi) extends Controller
  with MongoController
  with ReactiveMongoComponents {

  val mongoConfig = ConfigFactory.load().getConfig("mongodb")
  val mongoUri = mongoConfig.getString("uri")

  val mdriver = MongoDriver()
  val parsedUri = MongoConnection.parseURI(mongoUri)
  val mConnection = parsedUri.map(driver.connection(_))
  val futureConnection = Future.fromTry(mConnection)
  val bridgeDb: Future[DefaultDB] = futureConnection.flatMap(_.database("bridge"))
  val usersCollection: Future[BSONCollection] = bridgeDb.map(_.collection("users"))

  implicit val emailReader: BSONDocumentReader[Email] = Macros.reader[Email]
  implicit val userReader: BSONDocumentReader[User] = Macros.reader[User]

  def findUserByEmail(id: String) = Action.async{
    val findById = BSONDocument("id" -> id)
    val data = usersCollection.map{ docs =>
    docs.find(findById).cursor[User].collect[Vector]()
    }
    data.map(x => Ok(x.toString))
  }
}
