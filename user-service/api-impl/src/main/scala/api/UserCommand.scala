package api

import api.response.{GeneratedIdDone, User}
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity
import play.api.libs.json.{Format, Json}

sealed trait UserCommand

case class GetUser(id: String) extends PersistentEntity.ReplyType[User] with UserCommand

object GetUser {
  implicit val format: Format[GetUser] = Json.format
}

case class RegisterClient(
                           company: String,
                           firstName: String,
                           lastName: String,
                           email: String,
                           username: String,
                           password: String
                         ) extends PersistentEntity.ReplyType[GeneratedIdDone] with UserCommand
object RegisterClient {
  implicit val format: Format[RegisterClient] = Json.format
}

case class CreateUser(
                       firstName: String,
                       lastName: String,
                       email: String,
                       username: String,
                       password: String
                     ) extends PersistentEntity.ReplyType[GeneratedIdDone] with UserCommand
object CreateUser {
  implicit val format: Format[CreateUser] = Json.format
}

case class GetIdentityState() extends PersistentEntity.ReplyType[User] with UserCommand
