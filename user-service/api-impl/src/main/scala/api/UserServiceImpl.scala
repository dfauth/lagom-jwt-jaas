package api

import api.authentication.AuthenticationServiceComposition.authenticated
import api.authentication.{JwtTokenUtil, Token, TokenContent}
import api.permissions.UserServicePermission
import api.repo.UserRepository
import api.request.UserCredentials
import api.response.{Role, User}
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.transport.NotFound
import com.lightbend.lagom.scaladsl.persistence.{PersistentEntityRef, PersistentEntityRegistry}
import com.lightbend.lagom.scaladsl.server.ServerServiceCall
import log.Logging
import thingy.permissions._
import util.PasswordHashing.hashPassword

import scala.concurrent.{ExecutionContext, Future}

class UserServiceImpl(
                       persistentEntityRegistry: PersistentEntityRegistry,
                       userRepository: UserRepository
                     )(implicit ec: ExecutionContext) extends UserService with Logging {

   override def createUser() = authenticated { (tokenContent, _) =>
      ServerServiceCall {
        request => {
          val ref:PersistentEntityRef[UserCommand] = persistentEntityRegistry.refFor[UserEntity](request.username)
          ref.ask(new CreateUserCommand(request.firstName, request.lastName, request.username, request.email, request.password))
        } // request
      } // ServerServiceCall
    } // authenticated

   override def createRole() = authenticated { (tokenContent, _) =>
      ServerServiceCall {
        request => {
          val ref:PersistentEntityRef[UserCommand] = persistentEntityRegistry.refFor[UserEntity](request.roleName)
          ref.ask(new CreateRoleCommand(request.roleName, request.description.getOrElse("")))
        } // request
      } // ServerServiceCall
    } // authenticated

  override def getUsers() = authenticated { (tokenContent, _) =>
    ServerServiceCall {
      request => authorize[Future[Set[User]]](UserServicePermission(actions = "find"),tokenContent) {
        userRepository.runFindUsers.map[Set[User]](
          _.foldLeft(Set[User]())((a,u)=>{
            a + new User(u.id, u.firstName.getOrElse(""), u.lastName.getOrElse(""), u.email, u.email)
          }) // foldLeft
        ) // map
      }
    }
  }

  override def associateRoles(): ServiceCall[Set[Role], Boolean] = ???

  override def getUser(id: Int) = authenticated { (tokenContent, _) =>
    ServerServiceCall {
      request => {
        userRepository.runFindUser(id).map[User] {
          case Some(u) => new User(u.id, u.firstName.getOrElse(""), u.lastName.getOrElse(""), u.email, u.email)
          case None => throw NotFound(s"no user with id ${id} found")
        }
      }
    }
  }

  override def getUserByUsername(userName:String) = authenticated { (tokenContent, _) =>
    ServerServiceCall {
      request => {
        userRepository.runFindByEmail(userName).map[User] {
          case Some(u) => new User(u.id, u.firstName.getOrElse(""), u.lastName.getOrElse(""), u.email, u.email)
          case None => throw NotFound(s"no user with username ${userName} found")
        }
      }
    }
  }

  override def getRoleByRolename(roleName:String) = authenticated { (tokenContent, _) =>
    ServerServiceCall {
      request => {
        userRepository.runFindRoleByName(roleName).map[Role] {
          case Some(u) => new Role(u.id, u.roleName, u.description)
          case None => throw NotFound(s"no role with rolename ${roleName} found")
        }
      }
    }
  }

  override def getRoles() = authenticated { (tokenContent, _) =>
    ServerServiceCall {
      request => {
        userRepository.runFindRoles.map[Set[Role]](
          _.foldLeft(Set[Role]())((a, r) => {
            a + new Role(r.id, r.roleName, r.description)
          }) // foldLeft
        ) // map
      }
    }
  }

  override def authenticate(): ServiceCall[UserCredentials, Token] = {
    ServerServiceCall {
      request => {
        logger.info("request.username: "+request.username)
        userRepository.runFindByCredentials(request.username, hashPassword(request.password)).map[Token] {
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
