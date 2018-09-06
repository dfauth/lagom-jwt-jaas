package api.exception

import com.lightbend.lagom.scaladsl.api.transport.{ExceptionMessage, TransportErrorCode, TransportException}

object NotAuthenticated {
  val ErrorCode = TransportErrorCode(401, 4401, "Not Authenticated")

  def apply(message: String) = new NotAuthenticated(
    ErrorCode,
    new ExceptionMessage(classOf[NotAuthenticated].getSimpleName, message), null
  )

  def apply(cause: Throwable) = new NotAuthenticated(
    ErrorCode,
    new ExceptionMessage(classOf[NotAuthenticated].getSimpleName, cause.getMessage), cause
  )
}

final class NotAuthenticated(errorCode: TransportErrorCode, exceptionMessage: ExceptionMessage, cause: Throwable) extends TransportException(errorCode, exceptionMessage, cause) {
  def this(errorCode: TransportErrorCode, exceptionMessage: ExceptionMessage) = this(errorCode, exceptionMessage, null)
}

