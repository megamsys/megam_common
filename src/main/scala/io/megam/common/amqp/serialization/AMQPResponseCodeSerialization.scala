/*
** Copyright [2013-2016] [Megam Systems]
**
** https://opensource.org/licenses/MIT
**
*/
package io.megam.common.amqp.serialization


import scalaz._
import scalaz.Validation._
import scalaz.effect.IO
import scalaz.NonEmptyList._
import Scalaz._
import net.liftweb.json._
import net.liftweb.json.scalaz.JsonScalaz._
import net.liftweb.json.scalaz.JsonScalaz.JSONR
import net.liftweb.json.scalaz.JsonScalaz.JSONW
import io.megam.common.enumeration._
import io.megam.common.amqp.response.AMQPResponseCode

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
