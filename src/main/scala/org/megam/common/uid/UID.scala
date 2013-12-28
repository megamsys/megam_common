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
package org.megam.common.uid

import scalaz._
import scalaz.Validation._
import scalaz.NonEmptyList._

import Scalaz._
import org.apache.thrift.transport.{ TTransport }

/**
 * @author ram
 *
 */
class UID(hostname: String, port: Int, agent: String, soTimeoutMS: Int = 5000) {
 
  //lazy val, just evaluates once, we'll make it eval everytime you call.
  private def service: UniqueIDService = USnowflakeClient.create(hostname, port, soTimeoutMS)

  def get: ValidationNel[Throwable, UniqueID] = {
    (fromTryCatch {
      service._2.get_id(agent)
    } leftMap { t: Throwable =>
      new Throwable(
        """Unique ID Generation failure for 'agent:' '%s'
            |
            |Please verify your ID Generation server host name ,port and timeout. Our servers may be busy, increase the timeout and try again.
            |Refer the stacktrace for more information. If this error persits, ask for help on the forums.""".format(agent).stripMargin + "\n ", t)
    }).toValidationNel.flatMap { i: Long => Validation.success[Throwable, UniqueID](UniqueID(agent, i)).toValidationNel }
  }

}

object UID {

  def apply(host: String, port: Int, agent: String) = new UID(host, port, agent)

}
