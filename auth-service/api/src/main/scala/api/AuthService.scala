package api

import akka.NotUsed
import api.request.UserLogin
import api.response.{TokenRefreshDone, UserLoginDone}
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}
import com.lightbend.lagom.scaladsl.api.transport.Method

trait AuthService extends Service {
  def loginUser(): ServiceCall[UserLogin, UserLoginDone]
  def refreshToken(): ServiceCall[NotUsed, TokenRefreshDone]

  override final def descriptor = {
    import Service._

    named("identity-service").withCalls(
      restCall(Method.POST, "/api/auth/login", loginUser _),
      restCall(Method.PUT, "/api/auth/refresh", refreshToken _)
    ).withAutoAcl(true)
  }


}
