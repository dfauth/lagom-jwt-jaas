package api

import api.request.{ClientRegistration, UserCreation}
import api.response.GeneratedIdDone
import com.lightbend.lagom.scaladsl.api.ServiceCall

class UserServiceImpl extends UserService {

  override def registerClient(): ServiceCall[ClientRegistration, GeneratedIdDone] = ???

  override def createUser(): ServiceCall[UserCreation, GeneratedIdDone] = ???
}
