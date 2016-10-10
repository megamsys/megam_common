/*
** Copyright [2013-2016] [Megam Systems]
**
** https://opensource.org/licenses/MIT
**
*/
package io.megam.auth.funnel

import scalaz._
import Scalaz._
import scalaz.Validation
import scalaz.Validation.FlatMap._

import net.liftweb.json._
import net.liftweb.json.scalaz.JsonScalaz._
import java.nio.charset.Charset
import io.megam.common.jsonscalaz._
import io.megam.common.Constants._
import io.megam.auth.funnel.FunnelErrors._
import play.api.http.Status._

/**
 * @author ram
 *
 */
case class FunnelResponse(code: Int, msg: String, more: String, json_claz: String,
    msg_type: String = "error", links: String = tailMsg) {

  def toJValue: JValue = {
    import net.liftweb.json.scalaz.JsonScalaz.toJSON
    import io.megam.json.funnel.FunnelResponseSerialization
    val funser = new FunnelResponseSerialization()
    toJSON(this)(funser.writer)
  }

  def toJson(prettyPrint: Boolean = false): String = if (prettyPrint) {
    prettyRender(toJValue)
  } else {
    compactRender(toJValue)
  }

}

object FunnelResponse {

  def apply(code: Int, message: String, json_claz: String): FunnelResponse = new FunnelResponse(code, message, new String(), json_claz, "info")

  def apply(message: String, json_claz: String): FunnelResponse = FunnelResponse(OK, message, json_claz)

  def fromJValue(jValue: JValue)(implicit charset: Charset = UTF8Charset): Result[FunnelResponse] = {
    import net.liftweb.json.scalaz.JsonScalaz.fromJSON
    import io.megam.json.funnel.FunnelResponseSerialization
    val funser = new FunnelResponseSerialization()
    fromJSON(jValue)(funser.reader)
  }

  def fromJson(json: String): Result[FunnelResponse] = (Validation.fromTryCatchThrowable[net.liftweb.json.JValue,Throwable] {
    parse(json)
  } leftMap { t: Throwable =>
    UncategorizedError(t.getClass.getCanonicalName, t.getMessage, List())
  }).toValidationNel.flatMap { j: JValue => fromJValue(j) }


}
