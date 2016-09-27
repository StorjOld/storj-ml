package utils

case class QueryBuilder(size: Int, prettyPrint: Boolean, params: String*) {
  def build: String = {
    val paramString = params.fold("")(_ + "&" + _)
    val pretty = "pretty=" + prettyPrint.toString
    val sizeVal = "size=" + size.toString
    val query = paramString + "&" + sizeVal + "&" + pretty
    "p=" + query.takeRight(query.length - 1)
  }
}