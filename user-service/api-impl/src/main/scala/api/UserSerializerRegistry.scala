package api

import api.request.CreateUser
import api.response.{GeneratedIdDone, User}
import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}

import scala.collection.immutable

object UserSerializerRegistry extends JsonSerializerRegistry {

  override def serializers: immutable.Seq[JsonSerializer[_]] = immutable.Seq(
      JsonSerializer[GeneratedIdDone],
      JsonSerializer[CreateUser],
      JsonSerializer[RegisterClient],
      JsonSerializer[UserCreated],
      JsonSerializer[UserCreated],
      JsonSerializer[User]
    )
}
