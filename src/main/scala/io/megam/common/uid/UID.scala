/*
** Copyright [2013-2016] [Megam Systems]
**
** https://opensource.org/licenses/MIT
**
*/
package io.megam.common.uid

import scalaz._
import Scalaz._
import scalaz.Validation
import scalaz.Validation.FlatMap._
import scalaz.NonEmptyList._
import io.jvm.uuid._

/**
 * @author ram
 *
 */
class UID(agent: String) {


  def get: ValidationNel[Throwable, UniqueID] = {
    (Validation.fromTryCatchThrowable[Long,Throwable] {
      io.jvm.uuid.UUID.random.leastSigBits.abs
    } leftMap { t: Throwable =>
      new Throwable(
        """Unique id random gen failure for 'agent:' '%s'""".format(agent).stripMargin + "\n ", t)
    }).toValidationNel.flatMap { i: Long => Validation.success[Throwable, UniqueID](UniqueID(agent, i)).toValidationNel }
  }
}

object UID {

  def apply(agent: String) = new UID(agent)

}
