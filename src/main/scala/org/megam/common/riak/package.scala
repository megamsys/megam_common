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
package org.megam.common

import org.megam.common.riak.GSErrors._
/**
 * @author ram
 *
 */
package object riak {

  implicit class RichThrowable(thrownExp: Throwable) {
    def fold[T](connError: GSConnectionError => T,
      buckCreateError: BucketCreateError => T,
      fetchBuckError: BucketFetchError => T,
      storeError: BucketStoreError => T,
      anyError: Throwable => T): T = thrownExp match {
      case c @ GSConnectionError(_)      => connError(c)
      case b @ BucketCreateError(_, _)   => buckCreateError(b)
      case f @ BucketFetchError(_, _, _) => fetchBuckError(f)
      case s @ BucketStoreError(_, _, _) => storeError(s)
      case t @ _ => anyError(t)
    }
  }

    

  
}