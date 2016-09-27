package actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.event.LoggingReceive
import services.prediction.Predictor

class Director(eventServer: ActorRef) extends Actor with ActorLogging {
  import Director._

  val dataHandler = context.actorOf(Props[DataHandler], "data-handler")

  val batchTrainer = context.actorOf(BatchTrainer.props(self), "batch-trainer")

  val predictor = new Predictor

  val classifier = context.actorOf(Classifier.props(dataHandler, batchTrainer, predictor), "classifier")

  context.actorOf(TrainingDataInitializer.props(batchTrainer, eventServer), "training-data-initializer")


  override def receive = LoggingReceive {

    case GetClassifier => sender ! classifier

    case BatchTrainingFinished => batchTrainer ! GetLatestModel

    case undefined => log.info(s"Unexpected message $undefined")
  }
}

object Director {
  def props(eventServer: ActorRef) = Props(new Director(eventServer))

  case object GetClassifier

  case object BatchTrainingFinished

}