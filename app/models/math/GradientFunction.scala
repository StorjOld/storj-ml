package models.math

import breeze.linalg.DenseVector
import models.preprocessing._

case class GradientFunction(f: (DataFrame, DenseVector[Double]) => DenseVector[Double]) {

	def apply(data: DataFrame, weights: DenseVector[Double]) = f(data, weights)
}

case class CostFunction(f: (DataFrame, DenseVector[Double]) => Double) {

	def apply(data: DataFrame, weights: DenseVector[Double]) = f(data, weights)
}

