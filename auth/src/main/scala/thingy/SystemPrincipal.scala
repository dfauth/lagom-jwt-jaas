package thingy

import java.security.Principal

import api.authentication.TokenContent
import play.api.libs.json.{Format, Json}

class SystemPrincipal(principalType:String, system:String, name:String) extends Principal {
  override def getName: String = s"${principalType}:${system}:${name}"
}

case class UserPrincipal(name:String) extends SystemPrincipal("user", "auth", name)

case class RolePrincipal(name:String) extends SystemPrincipal("role", "auth", name)

object SystemPrincipal {

  implicit val format: Format[TokenContent] = Json.format

}
