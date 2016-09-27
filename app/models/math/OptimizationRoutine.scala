package models.math

import breeze.linalg.DenseVector
import models.preprocessing.DataFrame
import models.math.Sampling._

//TODO: Refactor this for abstraction, but keep the speed.
//Also can we get some L-BFGS up here?

object OptimizationRoutine {

  private case class OptInfo (
                              private val data: DataFrame,
                              private val miniBatchFraction: Double,
                              private val currSeed: Long,
                              private val history: OptimizationResult,
                              private val costFn: CostFunction,
                              private val gradFn: GradientFunction
                              ) {

    val weights = history.weights.last
    private val histLen = history.cost.size
    lazy val sample = sampleMiniBatch(data, miniBatchFraction, currSeed)
    lazy val sampleSize = sample.map(_.target.activeSize).reduceLeft(_ + _)
    lazy val newCost = costFn(sample, weights)
    lazy val gradients = gradFn(sample, weights)
    lazy val prevDeltaW = history.weights(histLen - 1) :- history.weights(histLen - 2)
  }

  val stochasticGradientDescent   = WeightUpdate(
    f = (data: DataFrame,
         history: OptimizationResult,
         gradFn: GradientFunction,
         costFn: CostFunction,
         initAlpha: Double,
         momentum: Double,
         miniBatchFraction: Double,
         miniBatchIterNum: Int,
         seed: Long) => {

      val opt = OptInfo(data, miniBatchFraction, seed + miniBatchIterNum, history, costFn, gradFn)
      
      val eta = initAlpha / math.sqrt(opt.sampleSize * miniBatchIterNum)
      val mom: DenseVector[Double] = opt.prevDeltaW :* momentum
      val newWtsNoMom: DenseVector[Double] = opt.weights :- (opt.gradients :* eta)
      val gradWithMom = (opt.gradients :* eta) :+ momentum
      val newWtsWithMom = newWtsNoMom :+ momentum

      OptimizationResult(
        cost = history.cost :+ opt.newCost,
        weights = history.weights :+ newWtsWithMom,
        gradients = history.gradients :+ gradWithMom
      )

    }
  )

  val adaptiveGradientDescent = WeightUpdate(

    f = (data: DataFrame,
         history: OptimizationResult,
         gradFn: GradientFunction,
         costFn: CostFunction,
         initAlpha: Double,
         momentum: Double,
         miniBatchFraction: Double,
         miniBatchIterNum: Int,
         seed: Long) => {

      val opt = OptInfo(data, miniBatchFraction, seed + miniBatchIterNum, history, costFn, gradFn)
      
      val mom: DenseVector[Double] = opt.prevDeltaW :* momentum
      val adaGradDiag: DenseVector[Double] =
        history.gradients.foldLeft(
          DenseVector.zeros[Double](opt.weights.iterableSize)
        )(
            (acc: DenseVector[Double], item: DenseVector[Double]) => {
              val temp: Array[Double] = acc.toArray.zip(item.toArray).map(i => i._1 + math.pow(i._2, 2))
              new DenseVector[Double](temp)
            })
      val scaledByDiag = new DenseVector[Double](
        opt.gradients.toArray.zip(adaGradDiag.toArray).map(
          i =>
            initAlpha * i._1 / math.sqrt(i._2)
        )
      )
      val adaptiveGradientWeights = (opt.weights :- scaledByDiag) :+ momentum
      
      OptimizationResult(
        cost = history.cost :+ opt.newCost,
        weights = history.weights :+ adaptiveGradientWeights,
        gradients = history.gradients :+ scaledByDiag
      )

    }
  )
}
