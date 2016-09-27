package models.preprocessing

import scala.reflect.ClassTag

trait DataFrameMonadContext {
	def from[A : ClassTag](data: Iterable[A]): DataFrameMonad[A]
}

object DataFrameMonadContext {

	implicit val dataFrameTContext: DataFrameMonadContext = DataFrameMonadTContext
}
