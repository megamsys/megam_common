/*
** Copyright [2013-2016] [Megam Systems]
**
** https://opensource.org/licenses/MIT
**
*/
package io.megam.auth.stack

import scalaz._
import Scalaz._
import scalaz.Validation._
import scala.concurrent.Future

import jp.t2v.lab.play2.stackc.{ RequestWithAttributes, RequestAttributeKey }

import io.megam.common.Constants._
import io.megam.auth.stack.Role._
import io.megam.auth.funnel._
import io.megam.auth.funnel.FunnelErrors._
import play.api.mvc._
import play.api.libs.iteratee.Enumerator

/**
 * @author rajthilak
 *
 */
/*
 * sub trait for stackable controller, proceed method was override here for our request changes,
 * And result return in super trait proceed method,
 * when stack action is called then this stackable controller is executed
 */
trait RequestAttributeKeyConstants {

  case object APIKey extends RequestAttributeKey[Option[AuthBag]]

  case object AuthorityKey extends RequestAttributeKey[Authority]

}
