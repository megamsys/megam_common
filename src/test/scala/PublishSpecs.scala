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
//package test
import org.specs2._
import org.specs2.mutable._
import org.specs2.Specification
import org.megam.common.amqp._
import org.specs2.matcher.MatchResult
import com.stackmob.newman.response.{ HttpResponse, HttpResponseCode }
import com.stackmob.newman._
import com.stackmob.newman.dsl._

/**
 * @author rajthilak
 *
 */
class PublishSpecs extends Specification   {
  
  println("jhsgfhjdf")
   def is = 
    "ApacheHttpClientSpecs".title ^ end ^
      """
  ApacheHttpClient is the HttpClient implementation that actually hits the internet
  """ ^ end ^
      "The Client Should" ^
      // "Correctly do GET requests" ! Get().succeeds ^
      "Correctly do POST requests" ! Post().succeeds ^
      end
      
  trait Test {
    val uris = "uris"
    val exchange = "exchange"
    val queue = "queue1"
      val message1 = Messages("id" -> "test", "name" -> "Common")     
        println("Execute method")
      val client = new RabbitMQClient("localhost:5672","exchange","queue1")
      
      protected def execute[T](t: AMQPRequest, expectedCode: AMQPResponseCode = AMQPResponseCode.Ok)(fn: AMQPResponse => MatchResult[T]) = {
        println("jhsdfdfb")
      val r = t.executeUnsafe

      r.code must beEqualTo(expectedCode) and fn(r)
    }
    protected def ensureAMQPOk(h: AMQPResponse) = h.code must beEqualTo(AMQPResponseCode.Ok)
   }
  
     //  new RabbitMQClient(2,2,"uris","exchange","queue1")
     
  
 
  case class Post() extends Test {
         println("Execute method")
         def succeeds =  execute(client.publish(message1, message1))(ensureAMQPOk(_))
 
  } 
  
  
}




