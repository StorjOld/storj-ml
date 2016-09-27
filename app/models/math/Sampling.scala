package models.math

import models.preprocessing.{DataFrame, LabeledFeatureMatrix}
import org.apache.log4j.Logger
import scala.util.Random

object Sampling {

  @transient val log = Logger.getLogger(Sampling.getClass)

  def sample[A](coll: Traversable[A], sampleSize: Int,
                withReplacement: Boolean, seed: Long = System.nanoTime): IndexedSeq[A] = {

    val indexed = coll.toIndexedSeq

    val rand = new Random(seed)

    @annotation.tailrec
    def collect(seq: IndexedSeq[A], size: Int, acc: List[A]): List[A] = {
      if (size == 0) acc
      else {
        val index = rand.nextInt(seq.size)
        collect(seq.updated(index, seq(0)).tail, size - 1, seq(index) :: acc)
      }
    }

    def withRep: IndexedSeq[A] =
      for (i <- 1 to sampleSize)
        yield indexed(rand.nextInt(coll.size))

    if (withReplacement)
      withRep
    else
      collect(indexed, sampleSize, Nil).toIndexedSeq
  }

  def sampleMiniBatch(data: DataFrame,
                      miniBatchFraction: Double,
                      currSeed: Long): DataFrame = {

    val collCount = data.size

    val regularSampling = collCount >= math.ceil(1.0 / miniBatchFraction)

    if (regularSampling) {

      data.exactSample(fraction = miniBatchFraction, seed = currSeed)

    } else {

      data.map {

        case v: LabeledFeatureMatrix =>

          val size = v.target.activeSize
          val rounded = math.max(1, math.round(miniBatchFraction * size).toInt)

          val rowIndices = sample(
            coll = 0 until size,
            sampleSize = rounded,
            withReplacement = false,
            seed = currSeed
          )

          LabeledFeatureMatrix(
            target = v.target(rowIndices).toDenseVector,
            features = v.features(rowIndices, ::).toDenseMatrix
          )
      }
    }
  }
}