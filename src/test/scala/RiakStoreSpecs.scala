/* 
** Copyright [2012] [Megam Systems]
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
 * @author rajthilak
 *
 */

import org.specs2._
import scalaz._
import Scalaz._
import org.specs2.mutable._
import org.specs2.Specification
import org.megam.common.amqp._
import org.specs2.matcher.MatchResult
import org.megam.common.riak._
import com.stackmob.scaliak._
import com.basho.riak.client.query.indexes.{ RiakIndexes, IntIndex, BinIndex }
import com.basho.riak.client.http.util.{ Constants => RiakConstants }
import org.megam.common.Zoo

class RiakStoreSpecs extends Specification {

  def is =
    "RiakStoreSpecs".title ^ end ^
      """
  Riak  client which stores Account in Riak
    """ ^ end ^
      "The Riak fetch spec Should" ^
      "Correctly print fetch result for account " ! AccountStore.succeeds ^
      end

  private lazy val riak: GSRiak = GSRiak("http://localhost:8098/riak/", "samplenodes")

  case object AccountStore {

    val metadataKey = "Field"
    val metadataVal = "1002"
    val bindex = BinIndex.named("email")
    val bvalue = Set("sandy@megamsandbox.com")

    def succeeds = {
      val t: ValidationNel[Throwable, Option[GunnySack]] = riak.store(new GunnySack("key13", "{\"id\":\"1\",\"email\":\"sandy@megamsandbox.com\",\"api_key\":\"IamAtlas{74}NobodyCanSeeME#07\",\"authority\":\"user\"}", RiakConstants.CTYPE_TEXT_UTF8, None, Map(metadataKey -> metadataVal), Map((bindex, bvalue))))
      val keys = riak.fetch("nodejs")
      val res = t
      println("-->" + res)
      val expectedRes = 0
      //this comparison is wrong. it will always fail.
      res mustEqual expectedRes

    }
  }

}