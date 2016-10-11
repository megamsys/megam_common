/*
** Copyright [2013-2016] [Megam Systems]
**
** https://opensource.org/licenses/MIT
**
*/
package io.megam.common.amqp.response

/**
 * @author ram
 *
 */

import scalaz._
import Scalaz._
import io.megam.common.enumeration._

sealed abstract class AMQPResponseCode(val code: Int, override val stringVal: String) extends Enumeration

object AMQPResponseCode {

  implicit val AMQPResponseCodeEqual: Equal[AMQPResponseCode] = new Equal[AMQPResponseCode] {
    override def equal(a1: AMQPResponseCode, a2: AMQPResponseCode): Boolean = a1.code === a2.code
  }

  implicit def amqpResponseCodeToInt(a: AMQPResponseCode): Int = a.code

  object Accepted extends AMQPResponseCode(202, "Accepted")

  object ClientTimeout extends AMQPResponseCode(408, "Client Timeout")

  object NoContent extends AMQPResponseCode(204, "No Content")

  object NotAcceptable extends AMQPResponseCode(406, "Not Acceptable")

  object NotFound extends AMQPResponseCode(404, "Not Found")

  object NotModified extends AMQPResponseCode(304, "Not Modified")

  object Ok extends AMQPResponseCode(200, "Ok")

  object ServiceUnavailable extends AMQPResponseCode(503, "Service Unavailable")

  def fromInt(i: Int): Option[AMQPResponseCode] = i match {
    case Accepted.code           => Accepted.some
    case ClientTimeout.code      => ClientTimeout.some
    case NoContent.code          => NoContent.some
    case NotAcceptable.code      => NotAcceptable.some
    case NotFound.code           => NotFound.some
    case NotModified.code        => NotModified.some
    case Ok.code                 => Ok.some
    case ServiceUnavailable.code => ServiceUnavailable.some
    case _                       => none
  }
}
