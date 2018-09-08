package api

import akka.NotUsed
import api.request.{ClientRegistration, Role, User}
import api.response.GeneratedIdDone
import com.lightbend.lagom.scaladsl.api.ServiceCall

class UserServiceImpl extends UserService {

  override def registerClient(): ServiceCall[ClientRegistration, GeneratedIdDone] = ???

  override def createUser(): ServiceCall[User, GeneratedIdDone] = ???

  override def getUsers(): ServiceCall[NotUsed, Set[User]] = ???

  override def associateRoles(): ServiceCall[Set[Role], Boolean] = ???

  override def getUser(userId: Int): ServiceCall[NotUsed, User] = ???

  override def getRoles(userId: Option[Int]): ServiceCall[NotUsed, Set[Role]] = ???
}
