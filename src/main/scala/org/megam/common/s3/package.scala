/* 
** Copyright [2012-2013] [Megam Systems]
**
** Licensed under the Apache License, Version 2.0 (the "License");
** you may not use this file except in compliance with the License.
** You may obtain a copy of the License at
**
** http://www.apache.org/licenses/LICENSE-2.0
**
** Unless required by applicable law or agreed to in writing, software
** distributed under the License is distributed on an "AS IS" BASIS,
** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
** See the License for the specific language governing permissions and
** limitations under the License.
*/
package org.megam.common.s3

import org.megam.common.s3.S3Errors._
/**
 * @author rajthilak
 *
 */
package object s3 {

  implicit class RichThrowable(thrownExp: Throwable) {
    def fold[T](connError: S3ConnectionError => T,
      downError: DownloadError => T,
      listError: ListingError => T,
      anyError: Throwable => T): T = thrownExp match {
      case c @ S3ConnectionError(_, _) => connError(c)
      case f @ DownloadError(_)        => downError(f)
      case l @ ListingError(_, _)      => listError(l)
      case t @ _                       => anyError(t)
    }
  }

}