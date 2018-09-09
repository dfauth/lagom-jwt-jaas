package api

import java.util.UUID

import api.response.User
import play.api.libs.json.{Format, Json}


case class UserState(client: Option[Client]) {
  def addUser(user: User): UserState = client match {
    case None => throw new IllegalStateException("User can't be added before client is created")
    case Some(client) =>
      val newUsers =  client.users :+ user
      UserState(Some(client.copy(users = newUsers)))
  }
}
object UserState {
  implicit val format: Format[UserState] = Json.format
}

case class Client(
                   id: UUID,
                   company: String,
                   users: Seq[User] = Seq.empty
                 )
object Client {
  implicit val format: Format[Client] = Json.format
}

//case class User(
//                 id: UUID,
//                 firstName: String,
//                 lastName: String,
//                 email: String,
//                 username: String,
//                 password: String
//               )
//object User {
//  implicit val format: Format[User] = Json.format
//}