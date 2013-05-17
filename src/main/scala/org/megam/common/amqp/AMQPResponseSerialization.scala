/* 
** Copyright [2012] [Megam Systems]
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

import scalaz._
import scalaz.effect.IO
import scalaz.NonEmptyList._
import Scalaz._
import net.liftweb.json._
import net.liftweb.json.scalaz.JsonScalaz._
import net.liftweb.json.scalaz.JsonScalaz.JSONR
import net.liftweb.json.scalaz.JsonScalaz.JSONW
import org.megam.common.enumeration._
import java.util.Date
/**
 * @author rajthilak
 *
 */
object AMQPResponseSerialization extends SerializationBase[AMQPResponse] {
  protected val CodeKey = "code"
  //protected val MessagesKey = "id"
  protected val BodyKey = "body"
  protected val TimeReceivedKey = "time_received"

  override implicit val writer = new JSONW[AMQPResponse] {

    import org.megam.common.amqp.MessageJSONSerialization.{ writer => MessagesWriter }
    import org.megam.common.amqp.AMQPResponseCodeSerialization.{ writer => ResponseCodeWriter }

    override def write(h: AMQPResponse): JValue = {
      JObject(
        JField(CodeKey, toJSON(h.code)(ResponseCodeWriter)) ::
          JField(BodyKey, JString(h.bodyString())) ::
          JField(TimeReceivedKey, JInt(h.timeReceived.getTime)) ::
          Nil)
    }
  }

  override implicit val reader = new JSONR[AMQPResponse] {

    import org.megam.common.amqp.MessageJSONSerialization.{ reader => MessagesReader }
    import org.megam.common.amqp.AMQPResponseCodeSerialization.{ reader => ResponseCodeReader }

    override def read(json: JValue): Result[AMQPResponse] = {
      val codeField = field[AMQPResponseCode](CodeKey)(json)(ResponseCodeReader)
      //val messagesField = field[Messages](MessagesKey)(json)(MessagesReader)      
      val bodyField = field[String](BodyKey)(json)
      val timeReceivedField = field[Long](TimeReceivedKey)(json)
      (codeField |@| bodyField |@| timeReceivedField) {
        (code: AMQPResponseCode, body: String, timeReceivedMilliseconds: Long) =>
          AMQPResponse(code, body.getBytes(), new Date(timeReceivedMilliseconds))
      }
    }
  }
}