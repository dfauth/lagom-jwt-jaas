package api.request

import play.api.libs.json.{Format, Json}
import com.wix.accord.dsl._
import api.regex.Matchers
import api.validation.ValidationViolationKeys._

case class User(
  firstName: String,
  lastName: String,
  email: String,
  username: String,
  password: String
) extends WithUserFields

object User {
  implicit val format: Format[User] = Json.format

  implicit val userValidator = validator[User] { u =>
    u.firstName as notEmptyKey("firstName") is notEmpty
    u.lastName as notEmptyKey("lastName") is notEmpty
    u.email as matchRegexFullyKey("email") should matchRegexFully(Matchers.Email)
    u.username as notEmptyKey("username") is notEmpty
    u.username as forSizeKey("username") has size > 5
    u.password as notEmptyKey("password") is notEmpty
    u.password as forSizeKey("password")  has size > 7
  }
}