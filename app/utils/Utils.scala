package utils

import breeze.linalg.DenseVector
import models.math.OptimizationRoutine._
import models.math.Optimizer._
import models.math.WeightInitializer._
import models.math.WeightUpdate
import models.ml.LinearRegression._
import models.preprocessing.DataConversions._
import models.preprocessing.LabeledFeatureVector
import models.preprocessing.DataFrame

import scala.io.Source
import scala.util.Random

object Utils {

	def takeEveryNthElement[A](v: Vector[A], n: Int): Vector[A] = {
		v.zipWithIndex.collect{ case(x, y) if ((y + 1) % n) == 0 => x}
	}

	def readCSV(path: String): Vector[Vector[String]] = {
		Source.fromFile(path)
			.getLines()
			.map(_.split(",").map(_.trim).toVector)
			.toVector
	}

	def createTestDataFrame(n: Int, m: Double = 2.5, b: Double = 1): DataFrame = {
		val rand = new Random(42L)
		val numExamples = n
		val (intercept, slope) = (b, m)
		val feature = Seq.fill(numExamples)(rand.nextDouble())
		val targets = feature.map(i => intercept + slope * i + rand.nextDouble() / 100)

		val data = targets.zip(feature).map {
			case (y, x) => LabeledFeatureVector(DenseVector[Double](1.0D, x), y)
		}
		toDataFrame(data, 10)
	}

	val mockTrainingParameters = optimize(
		iter = 2,
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

	def mockStochasticGradientDescent = (df: DataFrame) => mockTrainingParameters(stochasticGradientDescent, df)

}
