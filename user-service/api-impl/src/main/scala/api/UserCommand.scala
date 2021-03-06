package api

import akka.Done
import api.repo.User
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity
import play.api.libs.json.{Format, Json}

sealed trait UserCommand // extends Jsonable

case class CreateUserCommand(firstName: String,
                       lastName: String,
                       email: String,
                       username: String,
                       password: String
                     ) extends PersistentEntity.ReplyType[Done] with UserCommand

object CreateUserCommand {
  implicit val format: Format[CreateUserCommand] = Json.format
}

case class CreateRoleCommand(roleName: String,
                             description: String) extends PersistentEntity.ReplyType[Done] with UserCommand

object CreateRoleCommand {
  implicit val format: Format[CreateRoleCommand] = Json.format
}

case class GetUser(id: String) extends PersistentEntity.ReplyType[User] with UserCommand

object GetUser {
  implicit val format: Format[GetUser] = Json.format
}

case class GetIdentityState() extends PersistentEntity.ReplyType[User] with UserCommand
