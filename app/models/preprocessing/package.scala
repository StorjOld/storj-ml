package models

package object preprocessing {
  type DataFrame = DataFrameMonad[LabeledFeatureMatrix]
}
