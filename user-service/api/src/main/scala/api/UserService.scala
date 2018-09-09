package api

import akka.NotUsed
import api.request.{ClientRegistration, Role}
import api.response.{GeneratedIdDone, User}
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}

trait UserService extends Service {
  def registerClient(): ServiceCall[ClientRegistration, GeneratedIdDone]
  def createUser(): ServiceCall[User, GeneratedIdDone]
  def getUser(userId:String): ServiceCall[NotUsed, User]
  def getUsers(): ServiceCall[NotUsed, Set[User]]
  def getRoles(id:Option[String] = None): ServiceCall[NotUsed, Set[Role]]
  def associateRoles(): ServiceCall[Set[Role], Boolean]

  override final def descriptor = {
    import Service._

    named("user-service").withCalls(
      restCall(Method.POST, "/api/user/registration", registerClient _),
      restCall(Method.GET, "/api/user/users", getUsers _),
      restCall(Method.GET, "/api/user/roles", getRoles _),
      restCall(Method.POST, "/api/user", createUser _),
      restCall(Method.GET, "/api/user/:id/roles", getRoles _),
      restCall(Method.POST, "/api/user/:id/roles", associateRoles _),
      restCall(Method.DELETE, "/api/user/:id/roles", associateRoles _)
    ).withAutoAcl(true)
  }

}
