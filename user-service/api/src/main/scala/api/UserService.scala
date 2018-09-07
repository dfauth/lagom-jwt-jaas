package api

import api.request.{ClientRegistration, UserCreation}
import api.response.GeneratedIdDone
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}
import com.lightbend.lagom.scaladsl.api.transport.Method

trait UserService extends Service {
  def registerClient(): ServiceCall[ClientRegistration, GeneratedIdDone]
  def createUser(): ServiceCall[UserCreation, GeneratedIdDone]

  override final def descriptor = {
    import Service._

    named("identity-service").withCalls(
      restCall(Method.POST, "/api/client/registration", registerClient _),
      restCall(Method.POST, "/api/user", createUser _)
    ).withAutoAcl(true)
  }


}
