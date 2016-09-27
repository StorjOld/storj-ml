package actors

import akka.actor.{Actor, Props}
import akka.event.LoggingReceive
import actors.DataHandler.{Fetch, FetchResponse}
import models.preprocessing.DataFrame
import play.api.libs.json._
import play.api.Logger
import utils.Utils.createTestDataFrame

 trait DataHandlerProxy extends Actor

  class DataHandler extends Actor with DataHandlerProxy {
    val log = Logger(this.getClass)

    override def receive = LoggingReceive {

    case Fetch(eventId) =>
      log.debug(s"Received Fetch message for eventId = $eventId from $sender")

      // Warning: This is a hack until I can figure out how actors
      // interact with futures via data services.
      // Or even if they should interact in this context.
      //TODO: Write websocket (or use streams) for on the fly classification
      val predictionData = createTestDataFrame(10)
      sender ! FetchResponse(eventId, predictionData)

    case undefined => log.warn(s"Unexpected message $undefined")
      }
    }

    object DataHandler {
      def props = Props(new DataHandler)
      case class Fetch(eventId: Int)
      case class FetchResponse(eventId: Int, predictionData: DataFrame)

      object Fetch {
        implicit val fetchFormat = Json.format[Fetch]
      }


    }

