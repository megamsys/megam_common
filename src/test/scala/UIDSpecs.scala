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
/**
 * @author subash
 *
 */

import org.specs2._
import org.specs2.mutable._
import org.specs2.Specification
import org.megam.common.amqp._
import org.specs2.matcher.MatchResult

import org.megam.common.uid.UID

class UIDSpecs extends Specification {

  def is =
    "UIDSpecs".title ^ end ^
      """
  UID is an implementation of TwitterSnowflakeId service 
  """ ^ end ^
      "The UID Client Should" ^
      "Correctly return a Unique ID for agent act" ! UIDActService().succeeds ^
      "Correctly return a Unique ID for agent nod" ! UIDActService().succeeds ^
      end

  def execute[T](t: Long, expectedPrefix: String)(fn: Long => MatchResult[T]) = {
    t.toString must startWith(expectedPrefix) and fn(t)
  }

  protected def ensureUIDOk(h: Long) = h must beGreaterThan(0L)

  case class UIDActService() {
    def succeeds = execute(UID("localhost", 5670, "act").get,"act")(ensureUIDOk(_))
  }

  case class UIDNodService() {
    def succeeds = execute(UID("localhost", 5670, "nod").get,"nod")(ensureUIDOk(_))
  }

}