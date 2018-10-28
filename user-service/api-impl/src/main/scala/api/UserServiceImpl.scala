package api

import akka.{Done, NotUsed}
import api.repo.UserRepository
import api.request.{CreateRole, CreateUser, Role}
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

   override def createRole(): ServiceCall[CreateRole, Done] = {
//   override def createRole() = authenticated { (tokenContent, _) =>
      ServerServiceCall {
        request => {
          val ref:PersistentEntityRef[UserCommand] = persistentEntityRegistry.refFor[UserEntity](request.roleName)
          ref.ask(new CreateRoleCommand(request.roleName, request.description))
        } // request
      } // ServerServiceCall
    } // authenticated

  override def getUsers(): ServiceCall[NotUsed, Set[User]] =  { //???
    ServerServiceCall {
      request => {
        userRepository.runFindUsers.map[Set[User]](
          _.foldLeft(Set[User]())((a,u)=>{
          a + new User(u.firstName.getOrElse(""), u.lastName.getOrElse(""), u.email, u.email)
        }) // foldLeft
        ) // map
      }
    }
  }

  override def associateRoles(): ServiceCall[Set[Role], Boolean] = ???

  override def getUser(id: String): ServiceCall[NotUsed, User] = ???

  override def getRoles(): ServiceCall[NotUsed, Set[Role]] = {
    ServerServiceCall {
      request => {
        userRepository.runFindRoles.map[Set[Role]](
          _.foldLeft(Set[Role]())((a, r) => {
            a + new Role(r.id.getOrElse(-1), r.roleName, r.description.getOrElse(""))
          }) // foldLeft
        ) // map
      }
    }
  }


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
