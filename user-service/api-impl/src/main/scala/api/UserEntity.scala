package api

import akka.Done
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity

class UserEntity extends PersistentEntity {
  override type Command = UserCommand
  override type Event = UserEvent
  override type State = UserState

  override def initialState: UserState = UserState(None)


  override def behavior: Behavior = {

    Actions()
      .onCommand[CreateUserCommand, Done] {
      case (CreateUserCommand(firstName, lastName, email, username, password), ctx, state) =>

        ctx.thenPersist(
          UserCreated(firstName,
            lastName,
            email,
            username,
            password)
        ) { _ =>
          ctx.reply(Done)
        }
    }.onCommand[CreateRoleCommand, Done] {
      case (CreateRoleCommand(roleName, description), ctx, state) =>
        ctx.thenPersist(
          RoleCreated(roleName,
            description)
        ) { _ =>
          ctx.reply(Done)
        }
    }
  }
}
