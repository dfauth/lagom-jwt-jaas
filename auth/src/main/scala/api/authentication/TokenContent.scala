package api.authentication

import play.api.libs.json.{Format, Json}

case class TokenContent(username: String, isRefreshToken: Boolean = false, roles:Set[String] = Set.empty[String])
object TokenContent {
  implicit val format: Format[TokenContent] = Json.format
}