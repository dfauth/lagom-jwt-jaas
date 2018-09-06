package api

import akka.NotUsed
import api.authentication.TokenContent
import api.request.{ClientRegistration, UserCreation, UserLogin}
import api.response.{GeneratedIdDone, TokenRefreshDone, UserLoginDone}
import api.validation.ValidationUtil.validate
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.transport.Forbidden

class IdentityServiceImpl extends IdentityService {

  override def registerClient(): ServiceCall[ClientRegistration, GeneratedIdDone] = ???

  override def loginUser(): ServiceCall[UserLogin, UserLoginDone] = ???

//  override def loginUser() = ServiceCall { request =>
//    def passwordMatches(providedPassword: String, storedHashedPassword: String) = SecurePasswordHashing.validatePassword(providedPassword, storedHashedPassword)
//
//    validate(request)
//
//    for {
//      maybeUser <- identityRepository.findUserByUsername(request.username)
//
//      token = maybeUser.filter(user => passwordMatches(request.password, user.hashedPassword))
//        .map(user =>
//          TokenContent(
//            clientId = user.clientId,
//            userId = user.id,
//            username = user.username
//          )
//        )
//        .map(tokenContent => JwtTokenUtil.generateTokens(tokenContent))
//        .getOrElse(throw Forbidden("Username and password combination not found"))
//    }
//      yield {
//        UserLoginDone(token.authToken, token.refreshToken.getOrElse(throw new IllegalStateException("Refresh token missing")))
//      }
//  }

  override def refreshToken(): ServiceCall[NotUsed, TokenRefreshDone] = ???

  override def createUser(): ServiceCall[UserCreation, GeneratedIdDone] = ???
}
