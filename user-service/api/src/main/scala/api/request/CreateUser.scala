package api.request

import api.regex.Matchers
import play.api.libs.json.{Format, Json}
import com.wix.accord.dsl._
import api.validation.ValidationViolationKeys._

case class CreateUser(firstName: String,
                      lastName: String,
                      email: String,
                      username: String,
                      password: String) extends WithUserFields {
}

object CreateUser {
  implicit val format: Format[CreateUser] = Json.format

  implicit val createUserValidator = validator[CreateUser] { c =>
    c.firstName as notEmptyKey("firstName") is notEmpty
    c.lastName as notEmptyKey("lastName") is notEmpty
    c.email as matchRegexFullyKey("email") should matchRegexFully(Matchers.Email)
    c.username as notEmptyKey("username") is notEmpty
    c.username as forSizeKey("username") has size > 5
    c.password as notEmptyKey("password") is notEmpty
    c.password as forSizeKey("password")  has size > 7
  }
}

case class CreateRole(roleName: String,
                      description: String) extends WithRoleFields

object CreateRole {
  implicit val format: Format[CreateRole] = Json.format

  implicit val createRoleValidator = validator[CreateRole] { c =>
    c.roleName as notEmptyKey("roleName") is notEmpty
  }
}