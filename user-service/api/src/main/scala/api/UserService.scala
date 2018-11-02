package api

import akka.{Done, NotUsed}
import api.authentication.Token
import api.request.{CreateRole, CreateUser, UserCredentials}
import api.response.{Role, User}
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}

trait UserService extends Service {
  def getCurrentUser(): ServiceCall[NotUsed, User]
  def authenticate(): ServiceCall[UserCredentials, Token]
  def createUser(): ServiceCall[CreateUser, Done]
  def createRole(): ServiceCall[CreateRole, Done]
  def getUser(id:Int): ServiceCall[NotUsed, User]
  def getUserByUsername(username:String): ServiceCall[NotUsed, User]
  def getRoleByRolename(roleName:String): ServiceCall[NotUsed, Role]
  def getUsers(): ServiceCall[NotUsed, Set[User]]
  def getRoles(): ServiceCall[NotUsed, Set[Role]]
  def associateRoles(): ServiceCall[Set[Role], Boolean]

  override final def descriptor = {
    import Service._

    named("user-service").withCalls(
      restCall(Method.GET, "/api/user-service/info", getCurrentUser _),
      restCall(Method.POST, "/api/user-service/authenticate", authenticate _),
      restCall(Method.GET, "/api/user-service/users", getUsers _),
      restCall(Method.POST, "/api/user-service/users", createUser _),
      restCall(Method.GET, "/api/user-service/users/:id", getUser _),
      restCall(Method.GET, "/api/user-service/users?username", getUserByUsername _),
      restCall(Method.GET, "/api/user-service/roles", getRoles _),
      restCall(Method.POST, "/api/user-service/roles", createRole _),
      restCall(Method.GET, "/api/user-service/roles?rolename", getRoleByRolename _),
      restCall(Method.GET, "/api/user-service/roles/:id", getRoles _),
//      restCall(Method.POST, "/api/user-service/:id/roles", associateRoles _),
//      restCall(Method.DELETE, "/api/user-service/:id/roles", associateRoles _)
    ).withAutoAcl(true)
  }

}
