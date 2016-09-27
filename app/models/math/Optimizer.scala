package models.math

import models.preprocessing.DataFrame
import org.apache.log4j.Logger

object Optimizer {

  @transient val log = Logger.getLogger(Optimizer.getClass)

  def optimize(
                iter: Int,
                seed: Long = 42L,
                initAlpha: Double = 0.1,
                momentum: Double = 0.0,
                gradientFunction: GradientFunction,
                costFunction: CostFunction,
                updateFn: WeightUpdate,
                miniBatchFraction: Double,
                weightInitializer: WeightInitializer,
                data: DataFrame
                ): OptimizationResult = {

    val count = data.size
    val dataSize = data.headOption match {
      case Some(x) => x.features.cols
      case None => 0
    }
    val exampleCount = data.map(i => i.target.activeSize).reduceLeft(_ + _)
    val initWts = weightInitializer(dataSize, seed)
    val initGrads = weightInitializer(dataSize, seed + 1)
    val initCost = costFunction(data, initWts) / (miniBatchFraction * exampleCount)

    val initHistory = OptimizationResult(cost = Seq(initCost, initCost),
      weights = Seq(initWts, initWts),
      gradients = Seq(initGrads, initGrads))

    (1 to iter).foldLeft(initHistory) {

      case (history, it) =>

        if (it == iter)
          history
        else
          updateFn(data, history, gradientFunction, costFunction, initAlpha, momentum, miniBatchFraction, it, seed)
    }
  }
}