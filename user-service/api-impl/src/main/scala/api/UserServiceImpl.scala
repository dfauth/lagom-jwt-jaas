package api

import akka.NotUsed
import api.authentication.AuthenticationServiceComposition._
import api.request.{ClientRegistration, Role, WithUserFields}
import api.response.{GeneratedIdDone, User}
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.transport.BadRequest
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry
import com.lightbend.lagom.scaladsl.server.ServerServiceCall

import scala.concurrent.{ExecutionContext, Future}

class UserServiceImpl(
                       persistentEntityRegistry: PersistentEntityRegistry,
                       userRepository: UserRepository
                     )(implicit ec: ExecutionContext) extends UserService {

//  persistentEntityRegistry: PersistentEntityRegistry,
//  readSide: ReadSide,
//  myDatabase: MyDatabase
//
//  readSide.register[User](new UserEventProcessor(myDatabase))

  override def registerClient(): ServiceCall[ClientRegistration, GeneratedIdDone] = ???

  //  override def createUser(): ServiceCall[User, GeneratedIdDone] = {
//    val ref = persistentEntityRegistry.register(new UserEntity())
//    ref.ask(UseGreetingMessage(request.message))
   override def createUser() = authenticated { (tokenContent, _) =>
      ServerServiceCall { request =>
//        validate(request)

        def executeCommandCallback = () => {
          val ref = persistentEntityRegistry.refFor[UserEntity](tokenContent.clientId.toString)

          ref.ask(
            CreateUser(
              firstName = request.firstName,
              lastName = request.lastName,
              email = request.email,
              username = request.username,
              password = request.password
            )
          )
        }

        reserveUsernameAndEmail(request, executeCommandCallback)
      }
    }

  override def getUsers(): ServiceCall[NotUsed, Set[User]] = ???

  override def associateRoles(): ServiceCall[Set[Role], Boolean] = ???

  override def getUser(id: String): ServiceCall[NotUsed, User] = ???

//  req =>
//
//    val ref = persistentEntityRegistry.refFor[UserEntity](id)
//    ref.ask(GetUser(id))
    //db.run(userRepositiory.users)
//  }

  override def getRoles(id: Option[String]): ServiceCall[NotUsed, Set[Role]] = ???


  def reserveUsernameAndEmail[B](request: User, onSuccess: () => Future[B]): Future[B] = {
    def rollbackReservations(request: WithUserFields,usernameReserved: Boolean, emailReserved: Boolean) = {
      if (usernameReserved) {
        userRepository.unreserveUsername(request.username)
      }
      if (emailReserved) {
        userRepository.unreserveEmail(request.email)
      }
    }

    val canProceed = for {
      usernameReserved <- userRepository.reserveUsername(request.username)
      emailReserved <- userRepository.reserveEmail(request.email)
    }
      yield (usernameReserved, emailReserved)

    canProceed.flatMap(canProceed => {
      (canProceed._1 && canProceed._2) match {
        case true => onSuccess.apply()
        case false => {
          rollbackReservations(request, canProceed._1, canProceed._2)
          throw BadRequest("Either username or email is already taken.")
        }
      }
    })
  }

}
