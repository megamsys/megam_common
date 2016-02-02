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
package io.megam.util

/**
 * @author ram
 *
 */
class LongOverflowException(msg: String) extends Exception(msg)

object LongOverflowArith {
  def add(a: Long, b: Long) = {
    val c = a + b
    if (((a ^ c) & (b ^ c)) < 0)
      throw new LongOverflowException(a + " + " + b)
    else
      c
  }

  def sub(a: Long, b: Long) = {
    val c = a - b
    if (((a ^ c) & (-b ^ c)) < 0)
      throw new LongOverflowException(a + " - " + b)
    else
      c
  }

  def mul(a: Long, b: Long): Long = {
    if (a > b) {
      // normalize so that a <= b to keep conditionals to a minimum
      mul(b, a)
    } else if (a < 0L) {
      if (b < 0L) {
        if (a < Long.MaxValue / b) throw new LongOverflowException(a + " * " + b)
      } else if (b > 0L) {
        if (Long.MinValue / b > a) throw new LongOverflowException(a + " * " + b)
      }
    } else if (a > 0L) {
      // and b > 0L
      if (a > Long.MaxValue / b) throw new LongOverflowException(a + " * " + b)
    }

    a * b
  }
}