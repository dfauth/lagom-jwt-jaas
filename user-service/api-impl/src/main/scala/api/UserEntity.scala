package api

import com.lightbend.lagom.scaladsl.persistence.PersistentEntity

class UserEntity extends PersistentEntity {
  override type Command = UserCommand
  override type Event = UserEvent
  override type State = UserState

  override def initialState: UserState = ???

  override def behavior: Behavior = ???
}
