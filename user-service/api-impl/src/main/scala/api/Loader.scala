package api

import api.exception.handling.CustomExceptionSerializer
import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.persistence.jdbc.JdbcPersistenceComponents
import com.lightbend.lagom.scaladsl.persistence.slick.SlickPersistenceComponents
import com.lightbend.lagom.scaladsl.server._
import com.softwaremill.macwire._
import play.api.db.{HikariCPComponents, HikariCPConnectionPool}
import play.api.libs.ws.ahc.AhcWSComponents

class Loader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext): LagomApplication =
    new Application(context) {
      override def serviceLocator: ServiceLocator = NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new Application(context) with LagomDevModeComponents

  override def describeServices = List(
    readDescriptor[UserService]
  )
}

abstract class Application(context: LagomApplicationContext)
  extends LagomApplication(context)
    with JdbcPersistenceComponents
    with SlickPersistenceComponents
    with HikariCPComponents
    with AhcWSComponents
{

  // Bind the service that this server provides
  override lazy val lagomServer = serverFor[UserService](wire[UserServiceImpl])

  // connection pool
  override lazy val connectionPool = new HikariCPConnectionPool(environment)

  // Register the JSON serializer registry
  override lazy val jsonSerializerRegistry = UserSerializerRegistry

  // Register dependencies
  lazy val identityRepository = wire[UserRepository]

  override lazy val defaultExceptionSerializer = new CustomExceptionSerializer(environment)

  // Register the public-transportation-services persistent entity
  persistentEntityRegistry.register(wire[UserEntity])

  // Register read side processors
  readSide.register(wire[UserEventProcessor])
}
