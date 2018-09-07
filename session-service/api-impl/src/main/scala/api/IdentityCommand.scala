package api

import com.lightbend.lagom.scaladsl.persistence.PersistentEntity
import api.response.IdentityStateDone

sealed trait IdentityCommand

case class GetIdentityState() extends PersistentEntity.ReplyType[IdentityStateDone] with IdentityCommand