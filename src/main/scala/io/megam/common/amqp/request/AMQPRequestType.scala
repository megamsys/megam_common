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