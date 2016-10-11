/*
** Copyright [2013-2016] [Megam Systems]
**
** https://opensource.org/licenses/MIT
**
*/
package io.megam.util

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
 * The system [[io.megam.util.Stopwatch]] measures elapsed time
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
 * A trivial implementation of [[io.megam.util.Stopwatch]] for use as a null
 * object.
 */
object NilStopwatch extends Stopwatch {
  def start(): Elapsed = () => Duration.Bottom
}
