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

trait NoStacktrace extends Exception {
  override def fillInStackTrace = this
  // specs expects non-empty stacktrace array
  this.setStackTrace(NoStacktrace.NoStacktraceArray)
}

object NoStacktrace {
  val NoStacktraceArray = Array(new StackTraceElement("com.twitter.util", "NoStacktrace", null, -1))
}
