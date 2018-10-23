package util

import java.nio.charset.StandardCharsets
import java.security.MessageDigest

object PasswordHashing {
  def hashPassword(password: String) = {
    val digest = MessageDigest.getInstance("SHA-256")
    val encodedhash = digest.digest(password.getBytes(StandardCharsets.UTF_8))
    val buffer = new StringBuffer
    encodedhash
      .map(b => (0xFF & b).toHexString)
      .map(h => String.format("%s",h))
      .foldLeft(buffer)((buffer, h) => buffer.append(h))
    buffer.toString
  }
}


object Main extends App {
  PasswordHashing.hashPassword("password")
}
