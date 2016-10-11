/*
** Copyright [2013-2016] [Megam Systems]
**
** https://opensource.org/licenses/MIT
**
*/
/**
 * @author ram
 *
 *
 */
package io.megam.common

import net.liftweb.json.scalaz.JsonScalaz._

package object jsonscalaz {

  implicit class RichError(error: Error) {
    def fold[T](unexpected: UnexpectedJSONError => T,
      noSuchField: NoSuchFieldError => T,
      uncategorized: UncategorizedError => T): T = error match {
      case u @ UnexpectedJSONError(_, _)   => unexpected(u)
      case n @ NoSuchFieldError(_, _)      => noSuchField(n)
      case u @ UncategorizedError(_, _, _) => uncategorized(u)
    }
  }

}
