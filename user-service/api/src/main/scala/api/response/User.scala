package api.response

import api.regex.Matchers
import api.request.WithUserFields
import api.validation.ValidationViolationKeys._
import com.wix.accord.dsl._
import play.api.libs.json.{Format, Json}

case class User(
  id:Int,
  firstName: String,
  lastName: String,
  email: String,
  username: String,
) extends WithUserFields

object User {
  implicit val format: Format[User] = Json.format

  implicit val userValidator = validator[User] { u =>
    u.firstName as notEmptyKey("firstName") is notEmpty
    u.lastName as notEmptyKey("lastName") is notEmpty
    u.email as matchRegexFullyKey("email") should matchRegexFully(Matchers.Email)
    u.username as notEmptyKey("username") is notEmpty
    u.username as forSizeKey("username") has size > 5
  }
}