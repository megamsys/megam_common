/*
** Copyright [2013-2016] [Megam Systems]
**
** https://opensource.org/licenses/MIT
**
*/
package io.megam.auth.stack


/*
 * @author ram
 *
 */
package object stack {

  type ResultInError = Option[Tuple2[Int,String]]

  object ResultInError {
    def apply[C](m: Tuple2[Int,String]): ResultInError = Some(m)
  }

}
