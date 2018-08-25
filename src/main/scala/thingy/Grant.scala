package thingy

import thingy.Permission.PermissionFormat

/**
  * grant signedBy "signer_names", codeBase "URL",
        *principal principal_class_name "principal_name",
        *principal principal_class_name "principal_name",
        *... {
 **
 permission permission_class_name "target_name", "action",
          *signedBy "signer_names";
      *permission permission_class_name "target_name", "action",
          *signedBy "signer_names";
      *...
  *};
 *
 */
case class Grant(permissions:Set[Permission], principals:Set[String]) {

}

case class Permission(name:String, resource:String, action: String) {

}

object Permission {
  import play.api.libs.json._
  implicit object PermissionFormat extends Format[Permission] {

    // convert from JSON string to a Permission object (de-serializing from JSON)
    def reads(json: JsValue): JsResult[Permission] = {
      val name = (json \ "name").as[String]
      val resource = (json \ "resource").as[String]
      val action = (json \ "action").as[String]
      JsSuccess(Permission(name, resource, action))
    }

    // convert from Permission object to JSON (serializing to JSON)
    def writes(s: Permission): JsValue = {
      // JsObject requires Seq[(String, play.api.libs.json.JsValue)]
      val PermissionAsList = Seq("name" -> JsString(s.name),
        "resource" -> JsString(s.resource),
        "action" -> JsString(s.action)
      )
      JsObject(PermissionAsList)
    }
  }
}

object Grant {
  import play.api.libs.json._
  implicit object GrantFormat extends Format[Grant] {

    // convert from JSON string to a Grant object (de-serializing from JSON)
    def reads(json: JsValue): JsResult[Grant] = {
      val permissions = (json \ "permissions").as[Set[Permission]]
      val principals = (json \ "principals").as[Set[String]]
      JsSuccess(Grant(permissions, principals))
    }

    // convert from Grant object to JSON (serializing to JSON)
    def writes(s: Grant): JsValue = {
      // JsObject requires Seq[(String, play.api.libs.json.JsValue)]
      val GrantAsList = Seq("principals" -> JsArray(s.principals.map(p => JsString(p)).toIndexedSeq),
        "permissions" -> JsArray(s.permissions.map(p => PermissionFormat.writes(p)).toIndexedSeq))
      JsObject(GrantAsList)
    }
  }
}