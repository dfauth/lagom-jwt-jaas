package test;

import java.io.StringWriter
import java.util.Collections

import automat.MapBuilder.Value
import automat.{MapBuilder, Resource};

object TestResource {
  val AUTH = TestResource("/api/user-service/authenticate")
  val INFO = TestResource("/api/user-service/info")
  val USER = TestResource("/api/user-service/users")
  val ROLE = TestResource("/api/user-service/roles")
  val SUBSCRIPTION = TestResource("/api/user-service/stream")
}

case class TestResource(path:String, builder:MapBuilder[String,_] = new Value(Collections.emptyMap())) extends Resource {

  var params = Map[String,String]()

  def queryString(key: String, value: String): Resource = {
    params = params + (key -> value)
    this
  }

  override def uri:String = path

  override def queryString:String = {
    if(params.size == 0) {
      null
    } else {
      params.foldLeft(new StringWriter())((w,t) => {
        if(w.getBuffer.length() > 0) {
          w.append("&")
        }
        w.append(t._1).append("=").append(t._2)
      }).toString
    }
  };

  val map = builder.build()

}


