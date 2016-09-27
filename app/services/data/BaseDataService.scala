package services.data

trait BaseDataService[DB] {
    def loadData[A, B](id: A): Seq[B]
}
