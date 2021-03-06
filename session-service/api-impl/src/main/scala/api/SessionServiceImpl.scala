package api

import api.authentication.AuthenticationServiceComposition._
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry
import com.lightbend.lagom.scaladsl.server.ServerServiceCall

import scala.concurrent.ExecutionContext

class SessionServiceImpl(
                           persistentRegistry: PersistentEntityRegistry
                         )(implicit ec: ExecutionContext) extends SessionService
{
  override def getIdentityState() = authenticated { (tokenContent, _) =>
    ServerServiceCall { _ =>
      val ref = persistentRegistry.refFor[IdentityEntity](tokenContent.clientId.toString)

      ref.ask(GetIdentityState())
    }
  }
}
