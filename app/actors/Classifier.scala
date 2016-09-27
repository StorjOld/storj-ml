package actors

import actors.Classifier.Classify
import actors.DataHandler.Fetch
import akka.actor._
import akka.event.LoggingReceive
import breeze.linalg.DenseVector
import models.preprocessing.DataFrameMonad
import services.prediction.Predictor

class Classifier(dataHandler: ActorRef,
                   batchTrainer: ActorRef,
                   predictor: Predictor) extends Actor with ActorLogging {

    override def receive =  LoggingReceive {

      case Classify(eventId: Int) =>

        log.info(s"Start predicting attendance for event '$eventId'")

        val originalSender = sender

        val handler = context.actorOf(
          FetchResponseHandler.props(batchTrainer, originalSender, predictor), "fetch-response-message-handler")

        log.debug(s"Created handler $handler")

        dataHandler.tell(Fetch(eventId), handler)
    }
  }

  object Classifier {
    def props(dataHandler: ActorRef,
              batchTrainer: ActorRef,
              predictor: Predictor) = Props(new Classifier(dataHandler, batchTrainer, predictor))

    type PredictionVector = DataFrameMonad[DenseVector[Double]]

    case class Classify(eventId: Int)
    case class ClassificationResult(batchModelResult: PredictionVector)

    object ClassificationResult {

    }
  }









