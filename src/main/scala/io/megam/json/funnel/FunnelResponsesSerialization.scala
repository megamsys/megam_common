/*
** Copyright [2013-2016] [Megam Systems]
**
** https://opensource.org/licenses/MIT
**
*/
package io.megam.json.funnel

import scalaz._
import Scalaz._
import scalaz.effect.IO
import scalaz.EitherT._
import scalaz.Validation
import scalaz.Validation.FlatMap._
import scalaz.NonEmptyList._
import net.liftweb.json._
import net.liftweb.json.scalaz.JsonScalaz._
import io.megam.json.funnel._
import io.megam.auth.funnel._

/**
 * @author ram
 *
 */
object FunnelResponsesSerialization extends io.megam.json.SerializationBase[io.megam.auth.funnel.FunnelResponses] {

  implicit override val writer = new JSONW[FunnelResponses] {
    override def write(h: io.megam.auth.funnel.FunnelResponses): JValue = {
      val frsList: List[JValue] = h.map {
        frList: FunnelResponseList =>
          (frList.list.map { fr: FunnelResponse => fr.toJValue }).toList
      } | List[JObject]()


      JArray(frsList)
    }
  }

  implicit override val reader = new JSONR[io.megam.auth.funnel.FunnelResponses] {
    override def read(json: JValue): Result[io.megam.auth.funnel.FunnelResponses] = {
      json match {
        case JArray(jObjectList) => {
          val list = jObjectList.flatMap { jValue: JValue =>
            FunnelResponse.fromJValue(jValue) match {
              case Success(fr)   => List(fr)
              case Failure(fail) => List[FunnelResponse]()
            }
          } map { x: FunnelResponse => x }
          val frs: io.megam.auth.funnel.FunnelResponses = io.megam.auth.funnel.FunnelResponses(list)
          frs.successNel[Error]
        }
        case j => UnexpectedJSONError(j, classOf[JArray]).failureNel[io.megam.auth.funnel.FunnelResponses]
      }
    }
  }
}
