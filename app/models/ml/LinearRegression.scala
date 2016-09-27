package models.ml

import breeze.linalg.DenseVector
import models.math.{CostFunction, GradientFunction}
import models.preprocessing._


class LinearRegression {

}

object  LinearRegression {

	val linearRegressionCost = CostFunction(
		f = (data: DataFrame, weights: DenseVector[Double]) => {

			val counts = data.map(_.target.activeSize).reduceLeft(_ + _)

			val unscaledCost = data.aggregate(0.0D)(
				seqOp = {
					case (currCost, elem) => {
						currCost + (elem.features * weights :- elem.target).
							map(i => math.pow(i, 2)).reduceLeft(_ + _)
					}
				},
				combOp = {
					case (a, b) => a + b
				}
			)
			unscaledCost / (2 * counts)
		}
	)

	val linearRegressionGradient = GradientFunction(
		f = (data: DataFrame, weights: DenseVector[Double]) => {
			data.aggregate(DenseVector.zeros[Double](weights.iterableSize))(
				seqOp = {
					case (partialGrad: DenseVector[Double], labeledFeatureVector) =>
						val transpose = labeledFeatureVector.features.t
						transpose * (labeledFeatureVector.features * weights :- labeledFeatureVector.target)
				},
				combOp = {
					case (partVec1, partVec2) => partVec1 :+ partVec2
				}
			)
		}
	)
}
