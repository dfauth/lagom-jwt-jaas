package api.request

trait WithUserFields {
  val firstName: String
  val lastName: String
  val email: String
  val username: String
}

trait WithRoleFields {
  val roleName: String
  val description: Option[String]
}