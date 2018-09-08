package api.request

import api.validation.ValidationViolationKeys.notEmptyKey
import com.wix.accord.dsl._
import play.api.libs.json.{Format, Json}

case class Role(
  name: String,
  description: String
)

object Role {
  implicit val format: Format[Role] = Json.format

  implicit val roleValidator = validator[Role] { r =>
    r.name as notEmptyKey("name") is notEmpty
    r.description as notEmptyKey("description") is notEmpty
  }
}

