package api

import akka.NotUsed
import api.request.{ClientRegistration, UserCreation, UserLogin}
import api.response.{GeneratedIdDone, TokenRefreshDone, UserLoginDone}
import com.lightbend.lagom.scaladsl.api.ServiceCall

class IdentityServiceImpl extends IdentityService {

  override def registerClient(): ServiceCall[ClientRegistration, GeneratedIdDone] = ???

  override def loginUser(): ServiceCall[UserLogin, UserLoginDone] = ???

  override def refreshToken(): ServiceCall[NotUsed, TokenRefreshDone] = ???

  override def createUser(): ServiceCall[UserCreation, GeneratedIdDone] = ???
}
