package models.math;

import breeze.linalg.DenseVector

case class OptimizationResult(cost: Seq[Double],
                              weights: Seq[DenseVector[Double]],
                              gradients: Seq[DenseVector[Double]])

object OptimizationResult {

	def getTrainedWeights(opResult: OptimizationResult) = opResult.weights.last
}