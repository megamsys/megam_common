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


import java.util.concurrent.TimeUnit

import io.megam.util.Duration

object time {
  class RichWholeNumber(wrapped: Long) {
    def nanoseconds = Duration(wrapped, TimeUnit.NANOSECONDS)
    def nanosecond = nanoseconds
    def microseconds = Duration(wrapped, TimeUnit.MICROSECONDS)
    def microsecond = microseconds
    def milliseconds = Duration(wrapped, TimeUnit.MILLISECONDS)
    def millisecond = milliseconds
    def millis = milliseconds
    def seconds = Duration(wrapped, TimeUnit.SECONDS)
    def second = seconds
    def minutes = Duration(wrapped, TimeUnit.MINUTES)
    def minute = minutes
    def hours = Duration(wrapped, TimeUnit.HOURS)
    def hour = hours
    def days = Duration(wrapped, TimeUnit.DAYS)
    def day = days
  }

  private val ZeroRichWholeNumber = new RichWholeNumber(0) {
    override def nanoseconds = Duration.Zero
    override def microseconds = Duration.Zero
    override def milliseconds = Duration.Zero
    override def seconds = Duration.Zero
    override def minutes = Duration.Zero
    override def hours = Duration.Zero
    override def days = Duration.Zero
  }

  implicit def intToTimeableNumber(i: Int): RichWholeNumber =
    if (i == 0) ZeroRichWholeNumber else new RichWholeNumber(i)
  implicit def longToTimeableNumber(l: Long): RichWholeNumber =
    if (l == 0) ZeroRichWholeNumber else new RichWholeNumber(l)
}
