/*
** Copyright [2013-2016] [Megam Systems]
**
** https://opensource.org/licenses/MIT
**
*/
package io.megam.common.amqp.serialization

import scalaz._
import scalaz.NonEmptyList._
import Scalaz._
import net.liftweb.json._
import net.liftweb.json.scalaz.JsonScalaz._
import io.megam.common.enumeration._
import io.megam.common.amqp.response.{AMQPResponse, AMQPResponseCode}
import java.util.Date
import io.megam.common.amqp._

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

    import io.megam.common.amqp.serialization.MessageJSONSerialization.{ writer => MessagesWriter }
    import io.megam.common.amqp.serialization.AMQPResponseCodeSerialization.{ writer => ResponseCodeWriter }

    override def write(h: AMQPResponse): JValue = {
      JObject(
        JField(CodeKey, toJSON(h.code)(ResponseCodeWriter)) ::
          JField(BodyKey, JString(h.bodyString())) ::
          JField(TimeReceivedKey, JInt(h.timeReceived.getTime)) ::
          Nil)
    }
  }

  override implicit val reader = new JSONR[AMQPResponse] {

    import io.megam.common.amqp.serialization.MessageJSONSerialization.{ reader => MessagesReader }
    import io.megam.common.amqp.serialization.AMQPResponseCodeSerialization.{ reader => ResponseCodeReader }

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
