package api

import java.util.UUID

import api.authentication.AuthenticationServiceComposition._
import api.authentication.TokenContent
//import api.request.WithUserCreationFields
//import api.response.{TokenRefreshDone, UserLoginDone}
import api.validation.ValidationUtil._
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.transport.{BadRequest, Forbidden}
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry
import com.lightbend.lagom.scaladsl.server.ServerServiceCall
//import io.digitalcat.publictransportation.services.identity.impl.util.{JwtTokenUtil, SecurePasswordHashing}

import scala.concurrent.{ExecutionContext, Future}

class IdentityServiceImpl(
                           persistentRegistry: PersistentEntityRegistry
                         )(implicit ec: ExecutionContext) extends IdentityService
{
  override def getIdentityState() = authenticated { (tokenContent, _) =>
    ServerServiceCall { _ =>
      val ref = persistentRegistry.refFor[IdentityEntity](tokenContent.clientId.toString)

      ref.ask(GetIdentityState())
    }
  }
}
