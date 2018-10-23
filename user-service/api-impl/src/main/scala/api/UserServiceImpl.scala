package api

import akka.{Done, NotUsed}
import api.repo.UserRepository
import api.request.{CreateUser, Role}
import api.response.User
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.persistence.{PersistentEntityRef, PersistentEntityRegistry}
import com.lightbend.lagom.scaladsl.server.ServerServiceCall
import log.Logging

import scala.concurrent.ExecutionContext

class UserServiceImpl(
                       persistentEntityRegistry: PersistentEntityRegistry,
                       userRepository: UserRepository
                     )(implicit ec: ExecutionContext) extends UserService with Logging {

   override def createUser(): ServiceCall[CreateUser, Done] = {
//   override def createUser() = authenticated { (tokenContent, _) =>
      ServerServiceCall {
        request => {
          val ref:PersistentEntityRef[UserCommand] = persistentEntityRegistry.refFor[UserEntity](request.username)
          ref.ask(new CreateUserCommand(request.firstName, request.lastName, request.username, request.password, request.email))
        } // request
      } // ServerServiceCall
    } // authenticated

  override def getUsers(): ServiceCall[NotUsed, Set[User]] = ???
//    ServerServiceCall {
//      request => {
//        Future[Set[User]] {
//
//          Set(userRepository.findUsers.map(_.foldLeft(scala.collection.mutable.Set[User]())((a,u)=>{
//            a.add(new User(u.firstName.getOrElse(""), u.lastName.getOrElse(""), u.email, "fred", "password"))
//            a
//          })))
//        }
//      }
//    }
//  }

  override def associateRoles(): ServiceCall[Set[Role], Boolean] = ???

  override def getUser(id: String): ServiceCall[NotUsed, User] = ???

  override def getRoles(id: Option[String]): ServiceCall[NotUsed, Set[Role]] = ???


//  def doit[B](request: User, onSuccess: () => Future[B]): Future[B] = {
//    val canProceed = for {
//      event <- eventRepository.insert(Event(eventType = "create", payload = Json.toJson(request).toString()))
//    }
//      yield event
//
//    canProceed.flatMap(e => {
//      e match {
//        case e if(e.id.isDefined) => onSuccess.apply()
//        case _ => throw BadRequest("Request failed. Username is taken?")
//      }
//    })
//  }
}
