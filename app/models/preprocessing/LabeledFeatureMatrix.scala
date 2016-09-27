package models.preprocessing

import breeze.linalg.{DenseMatrix, DenseVector}

case class LabeledFeatureMatrix(features: DenseMatrix[Double], target: DenseVector[Double]) {
	override def toString: String =
		s"""VectorizedData(
		    |features = $features,
		    |target = $target
		    |)
       """.stripMargin
}