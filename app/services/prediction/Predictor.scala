package services.prediction

import actors.DataHandler.FetchResponse
import breeze.linalg.DenseVector
import models.math.OptimizationResult
import models.math.OptimizationResult._
import models.preprocessing.DataFrameMonad

trait PredictorProxy {

	type PredictionVector = DataFrameMonad[DenseVector[Double]]

	def predict(batchTrainingModel: OptimizationResult, fetchResponse: FetchResponse): PredictionVector
}

class Predictor extends  PredictorProxy {
	def predict(batchTrainerModel: OptimizationResult, fetchResponse: FetchResponse): PredictionVector = {
		  val trainedWeights = getTrainedWeights(batchTrainerModel)
		  fetchResponse.predictionData.map(x => x.features * trainedWeights)
	}
}

