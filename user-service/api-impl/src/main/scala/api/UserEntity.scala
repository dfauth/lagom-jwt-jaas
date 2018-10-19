package api

import akka.Done
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity
import util.PasswordHashing

class UserEntity extends PersistentEntity {
  override type Command = UserCommand
  override type Event = UserEvent
  override type State = UserState

  override def initialState: UserState = UserState(None)


  override def behavior: Behavior = {

    Actions()
      .onCommand[CreateUserCommand, Done] {
      case (CreateUserCommand(firstName, lastName, email, username, password), ctx, state) =>
        val hashedPassword = PasswordHashing.hashPassword(password)

        ctx.thenPersist(
          UserCreated(firstName,
            lastName,
            email,
            username,
            hashedPassword)
        ) { _ =>
          ctx.reply(Done)
        }
    }
  }
}
