package api

import api.request.{CreateRole, CreateUser}
import api.response.{Role, User}
import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}

import scala.collection.immutable

object UserSerializerRegistry extends JsonSerializerRegistry {

  override def serializers: immutable.Seq[JsonSerializer[_]] = immutable.Seq(
      JsonSerializer[CreateUser],
      JsonSerializer[CreateRole],
      JsonSerializer[Role],
      JsonSerializer[UserCreated],
      JsonSerializer[User]
    )
}
