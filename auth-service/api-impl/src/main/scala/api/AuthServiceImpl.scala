package api

import akka.NotUsed
import api.request.UserLogin
import api.response.{TokenRefreshDone, UserLoginDone}
import com.lightbend.lagom.scaladsl.api.ServiceCall

class AuthServiceImpl extends AuthService {

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

}
