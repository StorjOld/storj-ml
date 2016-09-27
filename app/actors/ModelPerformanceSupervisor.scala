package actors

import actors.BatchTrainer.BatchTrainerModel
import actors.ModelPerformanceSupervisor.TrainerType.TrainerType
import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.event.LoggingReceive
import models.preprocessing.DataFrame
import play.api.libs.json.{Json, Reads, Writes}

import utils.EnumeratorUtils

class ModelPerformanceSupervisor extends Actor with ActorLogging {

	import ModelPerformanceSupervisor._

	var clients = Set.empty[ActorRef]

	var df: Option[DataFrame] = None

	var batchTrainerModel: Option[BatchTrainerModel] = None

	override def receive = LoggingReceive {

		case batchModel: BatchTrainerModel =>
			batchTrainerModel = Some(batchModel)
			validateBatchModel(batchModel) foreach sendMessage

		case TrainingSet(c: DataFrame) =>
			df = Some(c)

		case Subscribe =>
			context.watch(sender)
			clients += sender
			for {
				model <- batchTrainerModel
				performance <- validateBatchModel(model)
			} yield sender ! performance

		case Unsubscribe =>
			context.unwatch(sender)
			clients -= sender

	}

	//TODO: Write actual performance implementation.
	def validateBatchModel(batchTrainerModel: BatchTrainerModel): Option[ModelPerformance] = {
		 Option(ModelPerformance("Regression", "1.0",   .99,    .99))
	}


	def sendMessage(msg: ModelPerformance) = clients.foreach(_ ! msg)

	def logStatistics(performance: ModelPerformance): Unit = {
		log.info(s"Trainer type: ${performance.trainer}")
		log.info(s"Current model: ${performance.model}")
		log.info(s"Area under the ROC curve: ${performance.areaUnderRoc}")
		log.info(s"Accuracy: ${performance.accuracy}")
	}

}

object ModelPerformanceSupervisor {

	def props() = Props(new ModelPerformanceSupervisor)

	object TrainerType extends Enumeration {

		type TrainerType = TrainerType.Value

		val Batch  = Value

		implicit val reads: Reads[TrainerType] = EnumeratorUtils.enumReads(TrainerType)

		implicit val writes: Writes[TrainerType] = EnumeratorUtils.enumWrites

	}

	case class TrainingSet(data: DataFrame)

	case class ModelPerformance(trainer: String, model: String, areaUnderRoc: Double, accuracy: Double)


	object ModelPerformance {

		implicit val formatter = Json.format[ModelPerformance]

	}

}
