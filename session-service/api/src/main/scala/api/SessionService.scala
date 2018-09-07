package api

import akka.NotUsed
import api.response.IdentityStateDone
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}

trait SessionService extends Service {
  def getIdentityState(): ServiceCall[NotUsed, IdentityStateDone]

  override final def descriptor = {
    import Service._

    named("session-service").withCalls(
      restCall(Method.GET, "/api/session/identity", getIdentityState _)
    ).withAutoAcl(true)

  }


}
