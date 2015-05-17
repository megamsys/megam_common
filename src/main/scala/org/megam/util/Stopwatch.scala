/* 
** Copyright [2013-2014] [Megam Systems]
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
package org.megam.util

/**
 * @author ram
 *
 */
/**
 * A stopwatch may be used to measure elapsed time.
 */
trait Stopwatch {
  type Elapsed = () => Duration

  /**
   * Start the stopwatch. The returned timer may be read any time,
   * returning the duration of time elapsed since start.
   */
  def start(): Elapsed
}

/**
 * The system [[org.megam.util.Stopwatch]] measures elapsed time
 * using [[System.nanoTime]].
 */
object Stopwatch extends Stopwatch {

  def start(): Elapsed = {
    val timeFn = Time.localGetTime().getOrElse(() => Time.fromNanoseconds(System.nanoTime()))
    val off = timeFn()
      () => timeFn() - off
  }

  def const(dur: Duration): Stopwatch = new Stopwatch {
    private[this] val fn = () => dur
    def start() = fn
  }
}

/**
 * A trivial implementation of [[org.megam.util.Stopwatch]] for use as a null
 * object.
 */
object NilStopwatch extends Stopwatch {
  def start(): Elapsed = () => Duration.Bottom
}