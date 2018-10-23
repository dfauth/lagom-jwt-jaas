package api.repo

import akka.stream.Materializer
import akka.stream.scaladsl.{Sink, Source}
import log.Logging
import slick.dbio.DBIOAction
import slick.jdbc.{JdbcBackend, JdbcProfile}

import scala.concurrent.Future

case class User(id: Option[Int] = None, email: String,
                firstName: Option[String] = None, lastName: Option[String] = None)

case class Role(id: Option[Int] = None, roleName: String,
                description: Option[String] = None)

case class UserRoleXref(userId: Int, roleId:Int)

trait Db {
  val profile: JdbcProfile
}

trait UsersTable { this: Db =>
  import profile.api._

  class Users(tag: Tag) extends Table[User](tag, "USERS") {
    // Columns
    def id = column[Int]("USER_ID", O.PrimaryKey, O.AutoInc)
    def email = column[String]("USER_EMAIL", O.Length(512), O.Unique)
    def firstName = column[Option[String]]("USER_FIRST_NAME", O.Length(64))
    def lastName = column[Option[String]]("USER_LAST_NAME", O.Length(64))

    // Indexes
    def emailIndex = index("USER_EMAIL_IDX", email, true)

    // Select
    def * = (id.?, email, firstName, lastName) <> (User.tupled, User.unapply)
  }

  val users = TableQuery[Users]
}

trait RolesTable { this: Db =>
  import profile.api._

  class Roles(tag: Tag) extends Table[Role](tag, "ROLES") {
    // Columns
    def id = column[Int]("ROLE_ID", O.PrimaryKey, O.AutoInc)
    def roleName = column[String]("ROLE_NAME", O.Length(512), O.Unique)
    def description = column[Option[String]]("DESCRIPTON", O.Length(64))

    // Indexes
    def roleNameIndex = index("ROLE_NAME_IDX", roleName, true)

    // Select
    def * = (id.?, roleName, description) <> (Role.tupled, Role.unapply)
  }

  val roles = TableQuery[Roles]
}

trait UserRoleMappingTable extends UsersTable with RolesTable { this: Db =>
  import profile.api._

  class UserRoleMapping(tag: Tag) extends Table[UserRoleXref](tag, "USER_ROLE_XREF") {
    // Columns
    def userId = column[Int]("USER_ID")
    def roleId = column[Int]("ROLE_ID")

    // primary key
    def pk = primaryKey("USER_ROLE_PK", (userId, roleId))
    def userFk = foreignKey("USER_FK", userId, users)(_.id, ForeignKeyAction.Restrict, ForeignKeyAction.Cascade)
    def roleFk = foreignKey("ROLE_FK", roleId, roles)(_.id, ForeignKeyAction.Restrict, ForeignKeyAction.Cascade)

    // Select
    def * = (userId, roleId)<>(UserRoleXref.tupled, UserRoleXref.unapply)
  }

  val userRoleMapping = TableQuery[UserRoleMapping]
}


class UserRepository(val profile: JdbcProfile, db:JdbcBackend#Database)
  extends Db with UsersTable with RolesTable with UserRoleMappingTable with Logging {

  import profile.api._

  import scala.concurrent.ExecutionContext.Implicits.global

  def probe():Future[Int] = {
    try {
      countRoles
    } catch {
      case t => {
        logger.info(s"probe encountered a throwable: ${t}")
        throw t
      }
    }
  }


  def init() = DBIOAction.seq(users.schema.create,
                              roles.schema.create,
                              userRoleMapping.schema.create,
                              populate
  )

  def populate() = {
    val user = new User(email = "administrator@domain.com")
    val role = new Role(roleName = "superuser")
//    (for {
//      u <- insert(user)
//      r <- insert(role)
//      s <- insert(u,r)
//    } yield {
//      s
//    }).result
    DBIOAction.seq(insert(role),insert(user)
    )
  }


  def runInit() = db.run(init)

  def drop() = DBIOAction.seq(userRoleMapping.schema.drop)
    .andThen(DBIOAction.seq(roles.schema.drop)
    .andThen(DBIOAction.seq(users.schema.drop)))

  def runDrop() = db.run(drop)

  def insert(user: User) = (users returning users.map(_.id) += user).map(id => user.copy(id = Some(id)))

  def runInsert(user: User) = db.run(insert(user))

  def insert(role: Role) = (roles returning roles.map(_.id) += role).map(id => role.copy(id = Some(id)))

  def runInsert(role: Role) = db.run(insert(role)) // .map(id => role.copy(id = Some(id)))

  def insert(user: User, role: Role) = userRoleMapping += UserRoleXref(user.id.get, role.id.get)

  def runInsert(user: User, role: Role) = db.run(insert(user, role))

  def find(user:User) =
    db.run((for (u <- users if u.id === user.id) yield u).result.headOption)

  def findByEmail(email:String) =
    (for (u <- users if u.email === email) yield u).result.headOption

  def runFindByEmail(email:String) =
    db.run(findByEmail(email))

  def find(role:Role) =
    db.run((for (r <- roles if r.id === role.id) yield r).result.headOption)

  def findRoles(f: (Users, Roles) => Rep[Boolean]) = ??? //db.run(findRoles.filter(f).result)

  def findUsers = db.run(users.result)

  def findRoles = db.run(roles.result)

  def countRoles:Future[Int] = db.run[Int](roles.size.result)

  val query = for {
    ((user, _),role) <- users.join(userRoleMapping).on(_.id === _.userId).join(roles).on(_._2.roleId === _.id)
  }  yield (user, role)

  def findRolesForUser(user:User) = query.filter(t=>t._1.id === user.id).result

  def runFindRolesForUser(user:User) = db.run(findRolesForUser(user))

  def update(id: Int, firstName: Option[String], lastName: Option[String]) = {
    val query = for (user <- users if user.id === id)
      yield (user.firstName, user.lastName)
    db.run(query.update(firstName, lastName)) map { _ > 0 }
  }

  def delete(user: User) =
    db.run(users.filter(_.id === user.id).delete) map { _ > 0 }

  def delete(role: Role) =
    db.run(roles.filter(_.id === role.id).delete) map { _ > 0 }

  def delete(user: User, role: Role) = {
    db.run(userRoleMapping.filter(x=>(x.userId === user.id.get) && (x.roleId === role.id.get)).delete) map { _ > 0 }
  }


  def stream(implicit materializer: Materializer) = Source
    .fromPublisher(db.stream(users.result.withStatementParameters(fetchSize=10)))
    .to(Sink.fold[Seq[User], User](Seq())(_ :+ _))
    .run()

  def getNames(id: Int) = db.run(
    sql"select user_first_name, user_last_name from users where user_id = #$id"
      .as[(String, String)].headOption)

}
