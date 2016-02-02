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
package io.megam.concurrent

/**
 * @author ram
 *
 */
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicInteger

import io.megam.util.{Future, Promise, Try}

/**
 * Efficient ordered ''serialization'' of operations.
 *
 * '''Note:''' This should not be used in place of Scala's
 * `synchronized`, but rather only when serialization semantics are
 * required.
 */
trait Serialized {
  protected case class Job[T](promise: Promise[T], doItToIt: () => T) {
    def apply() {
      promise.update { Try { doItToIt() } }
    }
  }

  private[this] val nwaiters = new AtomicInteger(0)
  protected val serializedQueue: java.util.Queue[Job[_]] = new ConcurrentLinkedQueue[Job[_]]

  protected def serialized[A](f: => A): Future[A] = {
    val result = new Promise[A]

    serializedQueue add { Job(result, () => f) }

    if (nwaiters.getAndIncrement() == 0) {
      do {
        Try { serializedQueue.remove()() }
      } while (nwaiters.decrementAndGet() > 0)
    }

    result
  }
}