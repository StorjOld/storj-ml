package actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.event.LoggingReceive
import models.preprocessing.DataFrame
import utils.Utils.createTestDataFrame


class TrainingDataInitializer(batchTrainer: ActorRef,
                                  eventServer: ActorRef ) extends Actor with ActorLogging {

	import TrainingDataInitializer._
	var stop = false

	override def preStart() = self ! LoadFromDataSource

	override def receive = LoggingReceive {

		case Finish(trainingData: DataFrame) =>
			log.debug("Received Finish message")
			val recordCount  = trainingData.size
			val msg = s"Sending $recordCount records to batch trainer"
			log.info(msg)
			eventServer ! msg
			val trainMessage = Train(trainingData)
			batchTrainer ! trainMessage
			context.stop(self)
			eventServer ! "Data initialization finished"

		case LoadFromDataSource =>
			log.debug("Received LoadFromDataSource message")
			val msg = "Loading training data from data source"
			log.info(msg)
			eventServer ! msg

			// Warning: This is a hack until I can write actual implementation.
      val batchData = createTestDataFrame(10)
      // Spark has RDDs that are (obviously) distributed and therefore
			// easily cached. It might be worth using it just for that.
			// TODO: Look into how Akka optimizes for large data sets.
			self ! Finish(batchData)
	}

}

object TrainingDataInitializer {
	def props(batchTrainer: ActorRef, eventServer: ActorRef) =
		 Props(new TrainingDataInitializer(batchTrainer, eventServer ))

	case object LoadFromDataSource
	case class Finish(trainingData:  DataFrame)

}