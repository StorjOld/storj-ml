package models.domain.storj

import play.api.libs.json._

case class BucketEntry(id: String,
                       bucket: Bucket,
                       frame: Frame,
                       name: String,
                       renewal: String,
                       mimetype: String)

object BucketEntry {
  implicit val bucketEntryFormat = Json.format[BucketEntry]
}
