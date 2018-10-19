package api

import akka.Done
import api.repo.User
import api.response.GeneratedIdDone
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity
import play.api.libs.json.{Format, Json}

sealed trait UserCommand// extends Jsonable

case class CreateUserCommand(firstName: String,
                       lastName: String,
                       email: String,
                       username: String,
                       password: String
                     ) extends PersistentEntity.ReplyType[Done] with UserCommand

object CreateUserCommand {
  implicit val format: Format[CreateUserCommand] = Json.format
}

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

case class GetIdentityState() extends PersistentEntity.ReplyType[User] with UserCommand
