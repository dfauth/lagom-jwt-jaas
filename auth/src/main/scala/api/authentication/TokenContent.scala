package api.authentication

import java.util.UUID

import play.api.libs.json.{Format, Json}

case class TokenContent(clientId: UUID, userId: UUID, username: String, isRefreshToken: Boolean = false, roles:Set[String] = Set.empty[String])
object TokenContent {
  implicit val format: Format[TokenContent] = Json.format
}