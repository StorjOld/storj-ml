package actors

import actors.BatchTrainer.BatchTrainerModel
import actors.Classifier.ClassificationResult
import actors.DataHandler.FetchResponse
import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.event.LoggingReceive

import scala.concurrent.duration._
import models.math.OptimizationResult
import services.prediction.Predictor

class TrainingModelResponseHandler(fetchResponse: FetchResponse,
                                   originalSender: ActorRef,
                                   predictor: Predictor) extends Actor with ActorLogging {

	import TrainingModelResponseHandler._
	import context.dispatcher

	var batchTrainerModel: Option[OptimizationResult] = None

	def receive = LoggingReceive{
		case BatchTrainerModel(model) =>
			log.debug(s"Received batch trainer model: $model")
			batchTrainerModel =  model.collect{ case(x: OptimizationResult) => x}
			predict

		case TrainingModelRetrievalTimeout =>
			log.debug("Timeout occurred")
			sendResponseAndShutdown(TrainingModelRetrievalTimeout)
	}

	def predict = batchTrainerModel match {
		case Some(batchM)  =>

			log.debug("Weights received for classification")

			timeoutMessenger.cancel

			val batchModelResult = predictor.predict(batchM, fetchResponse)

			sendResponseAndShutdown(ClassificationResult(batchModelResult))
		case _ =>
	}

	def sendResponseAndShutdown(response: Any) = {
		originalSender ! response
		log.debug(s"Stopping context capturing actor $self")
		context.stop(self)
	}

	val timeoutMessenger = context.system.scheduler.scheduleOnce(45 seconds) {
		self ! TrainingModelRetrievalTimeout
	}
}

object TrainingModelResponseHandler {
	case object TrainingModelRetrievalTimeout

	def props(fetchResponse: FetchResponse,
	          originalSender: ActorRef,
	          predictor: Predictor) =
		Props(new TrainingModelResponseHandler(fetchResponse, originalSender, predictor))
}