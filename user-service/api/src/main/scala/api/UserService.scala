package api

import akka.{Done, NotUsed}
import api.request.{CreateRole, CreateUser}
import api.response.{Role, User}
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}

trait UserService extends Service {
  def createUser(): ServiceCall[CreateUser, Done]
  def createRole(): ServiceCall[CreateRole, Done]
  def getUser(userId:String): ServiceCall[NotUsed, User]
  def getUsers(): ServiceCall[NotUsed, Set[User]]
  def getRoles(): ServiceCall[NotUsed, Set[Role]]
  def associateRoles(): ServiceCall[Set[Role], Boolean]

  override final def descriptor = {
    import Service._

    named("user-service").withCalls(
      restCall(Method.GET, "/api/user", getUsers _),
      restCall(Method.GET, "/api/user/roles", getRoles _),
      restCall(Method.POST, "/api/user", createUser _),
      restCall(Method.POST, "/api/user/roles", createRole _),
      restCall(Method.GET, "/api/user/:id/roles", getRoles _),
      restCall(Method.POST, "/api/user/:id/roles", associateRoles _),
      restCall(Method.DELETE, "/api/user/:id/roles", associateRoles _)
    ).withAutoAcl(true)
  }

}
