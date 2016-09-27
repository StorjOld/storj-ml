package actors

import actors.DataHandler.FetchResponse
import actors.FetchResponseHandler.FetchResponseTimeout
import akka.actor.{Actor, ActorLogging, ActorRef, Props, Terminated}
import akka.event.LoggingReceive
import services.prediction.Predictor

import scala.concurrent.duration._

class FetchResponseHandler(batchTrainer: ActorRef,
                           originalSender: ActorRef,
                           predictor: Predictor) extends Actor with ActorLogging {

	def receive = LoggingReceive {
		case fetchResponse: FetchResponse =>
			timeoutMessenger.cancel()
			val handler = context.actorOf(
				TrainingModelResponseHandler.props(
					fetchResponse,
					originalSender,
					predictor), "training-model-response-message-handler")
			log.debug(s"Created handler $handler")
			batchTrainer ! (GetLatestModel, handler)
			context.watch(handler)
		case t: Terminated =>
			log.debug(s"Received Terminated message for training model response handler $t")
			context.stop(self)
		case FetchResponseTimeout =>
			log.debug("Timeout occurred")
			originalSender ! FetchResponseTimeout
			context.stop(self)
	}
	import context.dispatcher

	val timeoutMessenger = context.system.scheduler.scheduleOnce(5 seconds) {
		self ! FetchResponseTimeout
	}
}

object FetchResponseHandler {
	case object FetchResponseTimeout
	def props(batchTrainer: ActorRef, originalSender: ActorRef, predictor: Predictor) =
		Props(new FetchResponseHandler(batchTrainer, originalSender, predictor))
}


