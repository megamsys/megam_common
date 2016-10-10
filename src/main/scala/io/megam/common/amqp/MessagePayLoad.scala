/*
** Copyright [2013-2016] [Megam Systems]
**
** https://opensource.org/licenses/MIT
**
*/
package io.megam.common.amqp

import scalaz._
import Scalaz._
import scalaz.effect.IO
import scalaz.EitherT._
import scalaz.Validation
import scalaz.Validation.FlatMap._
import scalaz.NonEmptyList._
import net.liftweb.json._
import net.liftweb.json.scalaz.JsonScalaz._
import io.megam.common.jsonscalaz._
import io.megam.common.amqp._
import io.megam.common.amqp.serialization.MessagePayLoadSerialization
import Scalaz._

/**
 * @author rajthilak
 *
 */
case class MessagePayLoad(messages: Messages) {

  def toJValue: JValue = {
    import net.liftweb.json.scalaz.JsonScalaz.toJSON
    toJSON(this)(MessagePayLoadSerialization.writer)
  }

  def toJson(prettyPrint: Boolean = false): String = if (prettyPrint) {
    prettyRender(toJValue)
  } else {
    compactRender(toJValue)
  }

}

object MessagePayLoad {

  def fromJValue(jValue: JValue): Result[MessagePayLoad] = {
    import net.liftweb.json.JsonAST.JValue
    fromJSON(jValue)(MessagePayLoadSerialization.reader)

  }

  def fromJson(json: String): Result[MessagePayLoad] = (Validation.fromTryCatchThrowable[JValue,Throwable] {
    parse(json)
  } leftMap { t: Throwable =>
    UncategorizedError(t.getClass.getCanonicalName, t.getMessage, List())
  }).toValidationNel.flatMap { j: JValue => fromJValue(j) }
}
