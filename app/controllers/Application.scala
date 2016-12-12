package controllers

import javax.inject._

import actors.Classifier.{ClassificationResult, Classify, _}
import actors.Director.GetClassifier
import actors.FetchResponseHandler.FetchResponseTimeout
import actors.TrainingModelResponseHandler.TrainingModelRetrievalTimeout
import actors.{Director, ModelPerformanceSupervisor, _}
import akka.actor.{ActorRef, ActorSystem}
import akka.pattern._
import akka.stream.Materializer
import akka.util.Timeout
import play.api.Logger
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import play.api.libs.streams._
import scala.concurrent.duration._
import utils.TimeoutException

@Singleton
class Application @Inject()(implicit system: ActorSystem, materializer: Materializer) extends Controller {

  val log = Logger(this.getClass)

  val eventServer = system.actorOf(EventServer.props)

  val performanceSupervisor = system.actorOf(ModelPerformanceSupervisor.props())

  val director = system.actorOf(Director.props(eventServer), "director")

  implicit val timeout = Timeout(10 seconds)

  def classify(eventId: Int) = Action.async(parse.empty) { implicit request =>
    (for {
      classifier <- (director ? GetClassifier).mapTo[ActorRef]
      classificationResults <- (classifier ? Classify(eventId)).map{
        case c: ClassificationResult => c
        case TrainingModelRetrievalTimeout => throw TimeoutException("Training model timed out.")
        case FetchResponseTimeout => throw TimeoutException("Loading data timed out.")
      }
    } yield Ok(Json.toJson(classificationResults.toString))) recover handleException
  }

  private def handleException: PartialFunction[Throwable, Result] = {
    case to: TimeoutException =>
      eventServer ! to.msg
      GatewayTimeout(to.msg)
    case ex => InternalServerError(ex.getMessage)
  }

  def eventSocket = WebSocket.accept[String, String] { request =>
    log.debug("Client connected to event socket")
    ActorFlow.actorRef(out => EventListener.props(out, eventServer))
  }

  def performanceSocket = WebSocket.accept[JsValue, JsValue] { request =>
    log.debug("Client connected to performance socket")
    ActorFlow.actorRef(out => EventListener.props(out, performanceSupervisor))
  }
}
