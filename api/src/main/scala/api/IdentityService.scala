package api

import akka.NotUsed
import api.response.IdentityStateDone
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}

trait IdentityService extends Service {
  def getIdentityState(): ServiceCall[NotUsed, IdentityStateDone]

  override final def descriptor = {
    import Service._

    named("identity-service").withCalls(
      restCall(Method.GET, "/api/state/identity", getIdentityState _)
    ).withAutoAcl(true)

  }


}
