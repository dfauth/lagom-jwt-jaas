package log

import org.slf4j.LoggerFactory

trait Logging {}

object Logging {

  val logger = LoggerFactory.getLogger(getClass)

}
