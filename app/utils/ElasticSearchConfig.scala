package utils

case class ElasticSearchConfig(url: String,
                               port: Int,
                               index: String,
                               api: String) {
  def build: String = url + ":" + port.toString + "/" + index + "/" + api + "?"
}