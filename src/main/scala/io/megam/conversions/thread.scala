/*
** Copyright [2013-2016] [Megam Systems]
**
** https://opensource.org/licenses/MIT
**
*/
package io.megam
package conversions

/**
 * @author ram
 *
 */


import java.util.concurrent.Callable

/**
 * Implicits for turning a block of code into a Runnable or Callable.
 */
object thread {
  implicit def makeRunnable(f: => Unit): Runnable = new Runnable() { def run() = f }

  implicit def makeCallable[T](f: => T): Callable[T] = new Callable[T]() { def call() = f }
}
