  package models.math

  import models.preprocessing.DataFrame

  case class WeightUpdate(f: (DataFrame, OptimizationResult, GradientFunction, CostFunction,
    Double, Double, Double, Int, Long) => OptimizationResult) {
    def apply(
               data: DataFrame,
               history: OptimizationResult,
               gradientFunction: GradientFunction,
               costFunction: CostFunction,
               initAlpha: Double,
               momentum: Double,
               miniBatchFraction: Double,
               miniBatchIterNum: Int,
               seed: Long
               ): OptimizationResult =
      f(data,
        history,
        gradientFunction,
        costFunction,
        initAlpha,
        momentum,
        miniBatchFraction,
        miniBatchIterNum,
        seed)
  }
  



