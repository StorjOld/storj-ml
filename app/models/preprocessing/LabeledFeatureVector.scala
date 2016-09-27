package models.preprocessing

import breeze.linalg.DenseVector

case class LabeledFeatureVector(features: DenseVector[Double], target: Double) {
  override def toString: String =
    s"LabeledFeatureVector(features = $features, target = $target)"
}