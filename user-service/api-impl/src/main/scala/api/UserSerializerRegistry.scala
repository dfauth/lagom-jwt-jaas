package api

import api.response.{GeneratedIdDone, User}
import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}

import scala.collection.immutable

object UserSerializerRegistry extends JsonSerializerRegistry {

  override def serializers: immutable.Seq[JsonSerializer[_]] = immutable.Seq(
      JsonSerializer[GeneratedIdDone],
      JsonSerializer[CreateUser],
      JsonSerializer[RegisterClient],
//      JsonSerializer[IdentityStateDone],
      JsonSerializer[ClientCreated],
      JsonSerializer[UserCreated],
//      JsonSerializer[UserLogin],
//      JsonSerializer[UserLoginDone],
      JsonSerializer[UserCreated],
      JsonSerializer[User]
//      JsonSerializer[Token],
//      JsonSerializer[IdentityState]
    )
}
