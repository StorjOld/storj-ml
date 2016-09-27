package models.preprocessing

import scala.reflect.ClassTag

trait DataFrameMonad[A] {

	def map[B : ClassTag](f: A => B): DataFrameMonad[B]

	def reduceLeft(op: (A, A) => A): A

	def aggregate[B : ClassTag](zero: B)(seqOp: (B, A) => B, combOp: (B, B) => B): B

	def sortBy[B : ClassTag](f: (A) ⇒ B)(implicit ord: math.Ordering[B]): DataFrameMonad[A]

	def take(k: Int): Traversable[A]

	def toSeq: Seq[A]

	def flatMap[B : ClassTag](f: A => TraversableOnce[B]): DataFrameMonad[B]

	def groupBy[B : ClassTag](f: A => B): DataFrameMonad[(B, Iterable[A])]

	def sample(withReplacement: Boolean, fraction: Double, seed: Long): DataFrameMonad[A]

	def exactSample(fraction: Double, seed: Long): DataFrameMonad[A]

	def size: Long

	def headOption: Option[A]

	def zipWithIndex: DataFrameMonad[(A, Long)]

	def foreach(f: A => Unit): Unit
}

object DataFrameMonad {

	 implicit def traversable2DistData[A: ClassTag](l: Traversable[A]): DataFrameMonad[A] = DataFrameMonadT(l)
}
