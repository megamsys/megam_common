/* 
** Copyright [2012-2013] [Megam Systems]
**
** Licensed under the Apache License, Version 2.0 (the "License");
** you may not use this file except in compliance with the License.
** You may obtain a copy of the License at
**
** http://www.apache.org/licenses/LICENSE-2.0
**
** Unless required by applicable law or agreed to in writing, software
** distributed under the License is distributed on an "AS IS" BASIS,
** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
** See the License for the specific language governing permissions and
** limitations under the License.
*/
package org.megam.common.amqp

/**
 * @author ram
 *
 */

import scalaz._
import Scalaz._
import org.megam.common.enumeration._
import language.implicitConversions

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
    case Accepted.code => Accepted.some
     case ClientTimeout.code => ClientTimeout.some
    case NoContent.code => NoContent.some
    case NotAcceptable.code => NotAcceptable.some
    case NotFound.code => NotFound.some
    case NotModified.code => NotModified.some
    case Ok.code => Ok.some
    case ServiceUnavailable.code => ServiceUnavailable.some    
    case _ => none
  }
}
