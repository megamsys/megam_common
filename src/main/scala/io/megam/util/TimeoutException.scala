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
import java.util.concurrent.{TimeoutException => JUCTimeoutException}

// Now that this is inherits from the usual TimeoutException, we can move to
// j.u.c.TimeoutException during our next API break.
class TimeoutException(message: String) extends JUCTimeoutException(message)
