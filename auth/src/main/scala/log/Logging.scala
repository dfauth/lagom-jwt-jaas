package log

import org.slf4j.LoggerFactory

trait Logging {
  val logger = LoggerFactory.getLogger(classOf[Logging])
}

object Logging {

  val logger = LoggerFactory.getLogger(getClass)

}
