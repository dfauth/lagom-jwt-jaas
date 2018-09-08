package api

import com.lightbend.lagom.scaladsl.persistence.PersistentEntity

class UserEntity extends PersistentEntity {
  override type Command = this.type
  override type Event = this.type
  override type State = this.type

  override def initialState: UserEntity.this.type = ???

  override def behavior: Behavior = ???
}
