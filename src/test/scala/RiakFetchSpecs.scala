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

class RiakFetchSpecs extends mutable.Specification {

  private lazy val riak: GSRiak = GSRiak("http://localhost:8098/riak/", "predeftest6")
  val metadataKey = "Field"
  val metadataVal = "1002"
  val bindex = BinIndex.named("")
  val bvalue = Set("")

  "Riak fetch test" in {
    val t: ValidationNel[Throwable, List[String]] = riak.fetchIndexByValue(new GunnySack("email", "sandy@megamsandbox.com", RiakConstants.CTYPE_TEXT_UTF8, None, Map(metadataKey -> metadataVal), Map((bindex, bvalue))))
   
    val keys = riak.fetch("nodejs")   
    
    keys match {
      case Success(t) =>
        "Success of fetch value" >> {
         
          println("Value fetch success" + t.toList)
        }
      case Failure(t) =>
        "Failure of fetch value" >> {
          println("Value fetch failure")
        }
    }
    t match {
      case Success(t) =>
        "Success of fetch value" >> {
         
          println("Value fetch success" + t)
        }
      case Failure(t) =>
        "Failure of fetch value" >> {
          println("Value fetch failure")
        }
    }
  }

  /*def is =
    "RiakSpecs".title ^ end ^
      """
  UID is an implementation of TwitterSnowflakeId service 
  """ ^ end ^
      "The Riak Spec Should" ^
      "Correctly store the value with secondary indexes" ! Store().succeeds ^
      //"Correctly fetch the value using secondary index" ! Fetch().succeeds ^
      end

  def executeStore[T](t: ValidationNel[Throwable, Option[GunnySack]])(fn: ValidationNel[Throwable, Option[GunnySack]] => MatchResult[T]) = {
    /*val res = t match {
      case Success(v) => v
      case Failure(errThrown) => {
        println("*=----------------------------------------*\n")
        errThrown.head.printStackTrace       
       }
    }*/
  
    t must beEqualTo(t) and fn(t)
  }

  protected def ensureRiakOk(h: Option[GunnySack]) = h must beEqualTo(h)
   private lazy val riak: GSRiak = GSRiak("http://localhost:8098/riak/", "mybucket")
 
case class Store() {
      val metadataKey = "Field"
      val metadataVal = "1002"       
      val bindex = BinIndex.named("email")
      val bvalue = Set("val7")
    def succeeds = execute(riak.fetchIndexByValue(new GunnySack("key12", "val9",RiakConstants.CTYPE_TEXT_UTF8,None,Map(metadataKey -> metadataVal),Map((bindex, bvalue)))))(ensureRiakOk(_))
    }

  /*case class Fetch() {
    val metadataKey = "Field"
      val metadataVal = "1002"       
      val bindex = BinIndex.named("email")
      val bvalue = Set("val9")
    def succeeds = execute(riak.fetchIndexByValue(new GunnySack("email", "val9",RiakConstants.CTYPE_TEXT_UTF8,None,Map(metadataKey -> metadataVal),Map((bindex, bvalue)))))(ensureUIDOk(_))
  }*/
*/
}