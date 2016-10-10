/*
** Copyright [2013-2016] [Megam Systems]
**
** https://opensource.org/licenses/MIT
**
*/
package io.megam.common.amqp.request

import io.megam.common.enumeration._
import scalaz._
import Scalaz._

/**
 * @author ram
 *
 */
sealed abstract class AMQPRequestType(override val stringVal: String) extends Enumeration

object AMQPRequestType {
  object PUB extends AMQPRequestType("PUB")
  object SUB extends AMQPRequestType("SUB")

  implicit val AMQPRequestTypeToReader = upperEnumReader(PUB, SUB)
}
