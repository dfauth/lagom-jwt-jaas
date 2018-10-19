package api

import api.repo.UserRepository
import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.broker.kafka.LagomKafkaComponents
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.persistence.jdbc.JdbcPersistenceComponents
import com.lightbend.lagom.scaladsl.persistence.slick.SlickPersistenceComponents
import com.lightbend.lagom.scaladsl.server._
import com.softwaremill.macwire._
import play.api.db.HikariCPComponents
import play.api.libs.ws.ahc.AhcWSComponents

class Loader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext): LagomApplication =
    new UserServiceApplication(context) {
      override def serviceLocator: ServiceLocator = NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new UserServiceApplication(context) with LagomDevModeComponents

  override def describeService = Some(readDescriptor[UserService])
}

abstract class UserServiceApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with JdbcPersistenceComponents
    with SlickPersistenceComponents
    with HikariCPComponents
    with LagomKafkaComponents
    with AhcWSComponents {

  //val dbconfig = DatabaseConfig.forConfig[JdbcProfile]("blah")
  lazy val userRepository = wire[UserRepository]

  // Bind the service that this server provides
  override lazy val lagomServer = serverFor[UserService](wire[UserServiceImpl])

  // Register the JSON serializer registry
  override lazy val jsonSerializerRegistry = UserSerializerRegistry

  // Register the User persistent entity
  persistentEntityRegistry.register(wire[UserEntity])

  // Register read side processors
  readSide.register(wire[UserEventProcessor])
}