package models.ml

import breeze.optimize._
import breeze.linalg.{DenseVector, _}

trait MachineLearningModel[Algorithm] {

	def features: DenseMatrix[Double]

	def classifiers: DenseVector[Double]

	def costFunction: DenseMatrix[Double] => DenseVector[Double] => DenseVector[Double] => (Double, DenseVector[Double])

	def predictionFunction: DenseMatrix[Double] => DenseMatrix[Double]

	def trainedWeights: DenseVector[Double] = {
		val f = new DiffFunction[DenseVector[Double]] {
			def calculate(params: DenseVector[Double]) = costFunction(features)(classifiers)(params)
		}
		minimize(f, DenseVector.zeros[Double](features.cols) )
	}

	def fittedValues: DenseMatrix[Double] = predictionFunction(features.toDenseMatrix * trainedWeights.toDenseMatrix)

	def predict(newFeatureVec: DenseMatrix[Double]): DenseMatrix[Double] =
		predictionFunction(newFeatureVec.toDenseMatrix * trainedWeights.toDenseMatrix)

}

object MachineLearningModel {

	def apply[Algorithm: MachineLearningModel]: MachineLearningModel[Algorithm] = implicitly

	def features[Algorithm](implicit model: MachineLearningModel[Algorithm]) = model.features

	def classifiers[Algorithm](implicit model: MachineLearningModel[Algorithm]) = model.classifiers

	def costFunction[Algorithm](w: DenseVector[Double])(implicit model: MachineLearningModel[Algorithm]) =
		model.costFunction(model.features)(model.classifiers)(w)

	def trainedWeights[Algorithm](implicit model: MachineLearningModel[Algorithm]) = {
		val f = new DiffFunction[DenseVector[Double]] {
			def calculate(params: DenseVector[Double]) = model.costFunction(model.features)(model.classifiers)(params)
		}
		minimize(f, DenseVector.zeros[Double](features.cols) )
	}

	def predictionFunction[Algorithm](implicit model: MachineLearningModel[Algorithm]) = model.predictionFunction

	def fittedValues[Algorithm](implicit model: MachineLearningModel[Algorithm]) =
		model.predictionFunction(model.features.toDenseMatrix * model.trainedWeights.toDenseMatrix)

	def predict[Algorithm](newFeatureVec: DenseMatrix[Double])(implicit model: MachineLearningModel[Algorithm]) =
		model.predictionFunction(model.trainedWeights  * newFeatureVec)
}


