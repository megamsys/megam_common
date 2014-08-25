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
package org.megam.common.amqp.serialization


import scalaz._
import scalaz.Validation._
import scalaz.effect.IO
import scalaz.NonEmptyList._
import Scalaz._
import net.liftweb.json._
import net.liftweb.json.scalaz.JsonScalaz._
import net.liftweb.json.scalaz.JsonScalaz.JSONR
import net.liftweb.json.scalaz.JsonScalaz.JSONW
import org.megam.common.enumeration._
import org.megam.common.amqp.response.AMQPResponseCode

/**
 * @author rajthilak
 *
 */
object AMQPResponseCodeSerialization extends SerializationBase[AMQPResponseCode] {
  override implicit val writer = new JSONW[AMQPResponseCode] {
    override def write(h: AMQPResponseCode): JValue = JInt(h.code)
  }

  override implicit val reader = new JSONR[AMQPResponseCode] {
    override def read(json: JValue): Result[AMQPResponseCode] = {
      json match {
        case JInt(code) => fromTryCatchThrowable[Option[AMQPResponseCode],Throwable](AMQPResponseCode.fromInt(code.toInt)).fold(
          succ = {
            o: Option[AMQPResponseCode] =>
              o.map {
                c: AMQPResponseCode => c.successNel[Error]
              } | {
                UncategorizedError("response code", "Unknown Http Response Code %d".format(code), List()).failureNel[AMQPResponseCode]
              }
          },
          fail = { t: Throwable =>
            UncategorizedError("response code", t.getMessage, List()).failureNel[AMQPResponseCode]
          })
        case j => UnexpectedJSONError(j, classOf[JInt]).failureNel[AMQPResponseCode]
      }
    }
  }
}