package api

import akka.{Done, NotUsed}
import api.repo.UserRepository
import api.request.{CreateRole, CreateUser, UserCredentials}
import api.response.{Role, User}
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.transport.NotFound
import com.lightbend.lagom.scaladsl.persistence.{PersistentEntityRef, PersistentEntityRegistry}
import com.lightbend.lagom.scaladsl.server.ServerServiceCall
import log.Logging
import api.authentication.AuthenticationServiceComposition.authenticated
import api.authentication.{JwtTokenUtil, Token, TokenContent}

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
          a + new User(u.id, u.firstName.getOrElse(""), u.lastName.getOrElse(""), u.email, u.email)
        }) // foldLeft
        ) // map
      }
    }
  }

  override def associateRoles(): ServiceCall[Set[Role], Boolean] = ???

  override def getUser(id: Int): ServiceCall[NotUsed, User] = {
    ServerServiceCall {
      request => {
        userRepository.runFindUser(id).map[User] {
          case Some(u) => new User(u.id, u.firstName.getOrElse(""), u.lastName.getOrElse(""), u.email, u.email)
          case None => throw NotFound(s"no user with id ${id} found")
        }
      }
    }
  }

  override def getUserByUsername(userName:String): ServiceCall[NotUsed, User] = {
    ServerServiceCall {
      request => {
        userRepository.runFindByEmail(userName).map[User] {
          case Some(u) => new User(u.id, u.firstName.getOrElse(""), u.lastName.getOrElse(""), u.email, u.email)
          case None => throw NotFound(s"no user with username ${userName} found")
        }
      }
    }
  }

  override def getRoles(): ServiceCall[NotUsed, Set[Role]] = {
    ServerServiceCall {
      request => {
        userRepository.runFindRoles.map[Set[Role]](
          _.foldLeft(Set[Role]())((a, r) => {
            a + new Role(r.id, r.roleName, r.description.getOrElse(""))
          }) // foldLeft
        ) // map
      }
    }
  }

  override def authenticate(): ServiceCall[UserCredentials, Token] = {
    ServerServiceCall {
      request => {
        logger.info("request.username: "+request.username)
        userRepository.runFindByCredentials(request.username, request.password).map[Token] {
          case Some(u) => JwtTokenUtil.generateTokens(TokenContent(username = u.email))
          case None => {
            logger.info("Oops. username "+request.username+" not found with that password")
            throw NotFound(s"no user with credentials provided found")
          }
        }// map
      }
    }
  }

  override def getCurrentUser() = authenticated { (tokenContent, _) =>
    ServerServiceCall {
      request => {
        userRepository.runFindByEmail(tokenContent.username).map[User] {
          case Some(u) => new User(u.id, u.firstName.getOrElse(""), u.lastName.getOrElse(""), u.email, u.email)
          case None => throw NotFound(s"no user with username ${tokenContent.username} found")
        }// map
      }
    }
  }
}
