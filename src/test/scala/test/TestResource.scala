package test;

import java.util.Collections

import automat.MapBuilder.Value
import automat.{MapBuilder, Resource};

object TestResource {
  val AUTH = TestResource("/api/user/login")
  val IDENTITY = TestResource("/api/state/identity")
  val USER = TestResource("/api/user")
  val SUBSCRIPTION = TestResource("/api/stream")
}

case class TestResource(uri:String, builder:MapBuilder[String,_] = new Value(Collections.emptyMap())) extends Resource {
  val map = builder.build()

}


