/*
** Copyright [2013-2016] [Megam Systems]
**
** https://opensource.org/licenses/MIT
**
*/
package io.megam.common

import scalaz._
import Scalaz._

/**
 * @author ram
 *
 */
package object uid {

  type UniqueID = Option[(String, Long)]

  object UniqueID {

    def apply(genID: Long):UniqueID = UniqueID("BIR", genID)

    def apply(prefix: String, genID: Long):UniqueID = (prefix.toUpperCase, genID).some

    def empty = None
  }

}
