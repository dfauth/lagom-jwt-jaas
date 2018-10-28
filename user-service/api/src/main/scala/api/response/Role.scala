package api.response

import api.request.WithRoleFields
import api.validation.ValidationViolationKeys.notEmptyKey
import com.wix.accord.dsl._
import play.api.libs.json.{Format, Json}

case class Role (
  id: Int,
  roleName: String,
  description: String
)extends WithRoleFields

object Role {
  implicit val format: Format[Role] = Json.format

  implicit val roleValidator = validator[Role] { r =>
    r.roleName as notEmptyKey("roleName") is notEmpty
    r.description as notEmptyKey("description") is notEmpty
  }
}

