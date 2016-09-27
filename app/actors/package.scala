import actors.DataHandler.FetchResponse
import models.preprocessing.DataFrame

package object actors {
  case object GetLatestModel
  case class  Train(featureData: DataFrame)
  case class  GetFeatures(fetchResponse: FetchResponse)
  case object Subscribe
  case object Unsubscribe
}
