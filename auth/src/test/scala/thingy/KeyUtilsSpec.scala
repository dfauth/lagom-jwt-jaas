package thingy

import org.apache.logging.log4j.scala.Logging
import org.scalatest._
import thingy.KeyUtils.{f, findNearestKey}

import scala.collection.mutable

class KeyUtilsSpec extends FlatSpec with Matchers with Logging {

  "key ordering" should "work" in {

    val keys = Set("/api", "/api/instrument", "/api/user", "/api/account", "/api/instrument/blah", "/api/account/1", "/api/account/3")

    val tree = mutable.TreeMap[String, String]()
    keys.foreach(k => {
      tree.put(k, k)
    })

    logger.info("keys: "+tree.keys)

    f("/api/users") should be (Some("/api"))
    f("/api") should be (None)

    findNearestKey("/api/instrument/blah1", tree) should be (List("/api","/api/instrument"))
    findNearestKey("/api/blah", tree) should be (List("/api"))
    findNearestKey("/api", tree) should be (List("/api"))
    findNearestKey("/api/user", tree) should be (List("/api", "/api/user"))
    findNearestKey("/api/account", tree) should be (List("/api","/api/account"))
    findNearestKey("/api/instrument", tree) should be (List("/api","/api/instrument"))
    findNearestKey("/api/instrument/blah", tree) should be (List("/api","/api/instrument","/api/instrument/blah"))
    findNearestKey("/api/instrument/blah1", tree) should be (List("/api","/api/instrument"))
    findNearestKey("/api/account/1", tree) should be (List("/api","/api/account","/api/account/1"))
    findNearestKey("/api/account/2", tree) should be (List("/api","/api/account"))
    findNearestKey("/api/account/3", tree) should be (List("/api","/api/account","/api/account/3"))
    findNearestKey("/poo", tree) should be (Seq.empty[String])
    findNearestKey("poo", tree) should be (Seq.empty[String])
    findNearestKey("/", tree) should be (Seq.empty[String])
    findNearestKey("", tree) should be (Seq.empty[String])
    findNearestKey(null, tree) should be (Seq.empty[String])
  }
}


