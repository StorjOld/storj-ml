package actors

import actors.Director.BatchTrainingFinished
import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.event.LoggingReceive
import models.preprocessing.DataFrame
import models.math.OptimizationResult
import models.math.OptimizationRoutine._
import models.math.Optimizer._
import models.math.WeightInitializer._
import models.math.WeightUpdate
import models.ml.LinearRegression._

trait BatchTrainerProxy extends Actor

class BatchTrainer(director: ActorRef) extends Actor with ActorLogging with BatchTrainerProxy {
  import BatchTrainer._

  var model: Option[OptimizationResult] = None

  override def receive = LoggingReceive {

    case Train(featureData: DataFrame) =>

      log.debug("Received Train message with feature data")
      log.info("Starting batch training")

      // Warning: This is a hack until I can implement
      // this the right way.
      val modelTrainingConfig = optimize(
        iter = 200,
        seed = 123L,
        initAlpha = 0.1,
        momentum = 0.9,
        gradientFunction = linearRegressionGradient,
        costFunction = linearRegressionCost,
        _: WeightUpdate,
        miniBatchFraction = 0.1,
        weightInitializer = gaussianInitialization,
        _: DataFrame
      )

      val SGDWithMomentum = modelTrainingConfig(stochasticGradientDescent, featureData)

      SGDWithMomentum.weights.last

      model = Option(SGDWithMomentum)

      log.info("Batch training finished")

      director ! BatchTrainingFinished

    case GetLatestModel =>
      log.debug("Received GetLatestModel message")
      sender ! BatchTrainerModel(model)
      log.debug(s"Returned model $model")
  }
}

object BatchTrainer {
  def props(director: ActorRef) = Props(new BatchTrainer(director: ActorRef))
  case class BatchTrainerModel(model: Option[OptimizationResult])
  case class BatchFeatures(features: Option[DataFrame])

}


