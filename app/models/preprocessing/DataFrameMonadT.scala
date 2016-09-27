package models.preprocessing

import models.math.Sampling
import scala.reflect.ClassTag

case class DataFrameMonadT[A: ClassTag](ls: Traversable[A]) extends DataFrameMonad[A] {
	override def map[B : ClassTag](f: A => B): DataFrameMonad[B] =
		new DataFrameMonadT(ls.map(f))

	override def reduceLeft(op: (A, A) => A): A =
		ls.reduceLeft(op)

	override def aggregate[B : ClassTag](zero: B)(seqOp: (B, A) => B, combOp: (B, B) => B): B =
		ls.aggregate(zero)(seqOp, combOp)

	override def sortBy[B : ClassTag](f: (A) ⇒ B)(implicit ord: math.Ordering[B]): DataFrameMonad[A] =
		new DataFrameMonadT(ls.toSeq.sortBy(f))

	override def take(k: Int): Traversable[A] =
		ls.take(k)

	override def toSeq: Seq[A] =
		ls.toSeq

	override def flatMap[B : ClassTag](f: A => TraversableOnce[B]): DataFrameMonad[B] =
		new DataFrameMonadT(ls.flatMap(f))

	override def groupBy[B : ClassTag](f: A => B): DataFrameMonad[(B, Iterable[A])] =
		new DataFrameMonadT(ls.groupBy(f).map({ case (b, iter) => (b, iter.toIterable) }))

	override def sample(withReplacement: Boolean, fraction: Double, seed: Long): DataFrameMonad[A] =
		Sampling.sample(ls, math.round(fraction * ls.size).toInt, withReplacement, seed)

	override def exactSample(fraction: Double, seed: Long): DataFrameMonad[A] =
		sample(withReplacement = false, fraction = fraction, seed = seed)

	override def size: Long =
		ls.size

	override def headOption: Option[A] =
		ls.headOption

	override def zipWithIndex: DataFrameMonad[(A, Long)] =
		ls.toIndexedSeq.zipWithIndex.map(a => (a._1, a._2.toLong))

	override def foreach(f: A => Unit): Unit =
		ls.foreach(f)
}


case object DataFrameMonadTContext extends DataFrameMonadContext {

	import DataFrameMonad._

	override def from[A : ClassTag](data: Iterable[A]): DataFrameMonad[A]= data.toSeq
}