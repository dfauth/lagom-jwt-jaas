package api.authentication

import javax.security.auth.Subject
import play.api.libs.json.{Format, Json}
import thingy.{RolePrincipal, UserPrincipal}

case class TokenContent(username: String, isRefreshToken: Boolean = false, roles:Set[String] = Set.empty[String]){

  def toSubject():Subject = {
    val subject = new Subject()
    subject.getPrincipals.add(new UserPrincipal(username))
    val rolePrincipals = roles.map {
      roleName =>
        new RolePrincipal(roleName)
    }.foreach {
      subject.getPrincipals.add(_)
    }
    subject
  }

}

object TokenContent {

  implicit val format: Format[TokenContent] = Json.format

}