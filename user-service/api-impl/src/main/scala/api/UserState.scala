package api

import api.response.User
import play.api.libs.json.{Format, Json}


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
//
//case class Client(
//                   id: UUID,
//                   company: String,
//                   users: Seq[User] = Seq.empty
//                 )
//object Client {
//  implicit val format: Format[Client] = Json.format
//}

case class UserState(user:Option[User]) {
  def addUser(user: User): UserState = UserState(Some(user))
}

object UserState {
  implicit val format: Format[UserState] = Json.format
}


