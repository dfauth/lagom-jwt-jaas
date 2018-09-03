package api

import akka.NotUsed
import api.request.{ClientRegistration, UserCreation, UserLogin}
import api.response.{GeneratedIdDone, TokenRefreshDone, UserLoginDone}
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}
import com.lightbend.lagom.scaladsl.api.transport.Method

trait IdentityService extends Service {
  def registerClient(): ServiceCall[ClientRegistration, GeneratedIdDone]
  def loginUser(): ServiceCall[UserLogin, UserLoginDone]
  def refreshToken(): ServiceCall[NotUsed, TokenRefreshDone]
  def createUser(): ServiceCall[UserCreation, GeneratedIdDone]

  override final def descriptor = {
    import Service._

    named("identity-service").withCalls(
      restCall(Method.POST, "/api/client/registration", registerClient _),
      restCall(Method.POST, "/api/user/login", loginUser _),
      restCall(Method.PUT, "/api/user/token", refreshToken _),
      restCall(Method.POST, "/api/user", createUser _)
    ).withAutoAcl(true)
  }


}
