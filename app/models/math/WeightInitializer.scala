package models.math

import breeze.linalg.DenseVector
import scala.util.Random

case class WeightInitializer(f: (Int, Long) => DenseVector[Double]) {
  def apply(numEl: Int, seed: Long): DenseVector[Double] = f(numEl, seed)
}

object WeightInitializer {

  val gaussianInitialization = WeightInitializer(
    f = (numEl: Int, seed: Long) => {
      val rand = new Random(seed)
      new DenseVector[Double](Array.fill(numEl)(rand.nextGaussian()))
    }
  )
}
