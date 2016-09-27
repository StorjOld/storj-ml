package models.preprocessing

import breeze.linalg.{DenseMatrix, DenseVector}

object DataConversions {

  def toDataFrame(data: DataFrameMonad[LabeledFeatureVector], 
                             numFeatures: Int): DataFrame = {

    val exampleCount = data.size
    val numRecords = exampleCount / numFeatures

    val grouped = data.zipWithIndex.groupBy { case (_, indx) => indx % numRecords }
      .map { case (indx, iter) => iter.map 
             { case (vector, _) => vector }
      }
    
     grouped.map {
          case arr => val numFeat = arr.headOption.fold(0)(_.features.iterableSize)
            val init = LabeledFeatureMatrix(
              features = DenseMatrix.zeros[Double](0, numFeat),
              target = DenseVector.zeros[Double](0)
            )
            arr.foldLeft(init)(
              (acc, elem) => {
                val vecCat = DenseVector.vertcat(acc.target, DenseVector(elem.target))
                val featMat = elem.features.toDenseMatrix
                val matCat = DenseMatrix.vertcat(acc.features, featMat)
                LabeledFeatureMatrix(matCat, vecCat)
          })
    }
  }
}

