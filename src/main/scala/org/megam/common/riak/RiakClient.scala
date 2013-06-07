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
package org.megam.common.riak

import scalaz._
import Scalaz._
import scalaz.effect.IO
import com.stackmob.scaliak._

/**
 * @author ram
 *
 */
case class Bag(key: String, value: String)

class Riak(uri: String, bucketName: String) {
  import Riak._

  /**
   * Connect to the riak system using the scaliak client.
   */
  private lazy val client: ScaliakClient = Scaliak.httpClient(uri)

  /*
   * connect the existing bucket in riak client
   * if doesn't bucket then, create a new bucket
   */
  private val bucket: ScaliakBucket = {
    client.generateAndSetClientId()
    val bucket = client.bucket(bucketName).unsafePerformIO() match {
      case Success(b) => b
      case Failure(e) => throw e
    }
    bucket
  }

  /*
   * put the specified key and their value to riak bucket
   */
  def put(key: String, value: String) {
    if (bucket.store(new Bag(key, value)).unsafePerformIO().isFailure) {
      throw new Exception("failed to store object")
    }
  }

  /*
   * fetch a Bag object
   * and return the option node object
   */
  def fetch(key: String): Option[Bag] = {
    val fetchResult: ValidationNel[Throwable, Option[Bag]] = bucket.fetch(key).unsafePerformIO()
    fetchResult match {
      case Success(mbFetched) => {
        println(mbFetched some { v => v.key + ":" + v.value } none { "did not find key" })
        mbFetched
      }
      case Failure(es) => throw es.head
    }
  }

  private def printFetchRes(v: ValidationNel[Throwable, Option[Bag]]): IO[Unit] = v match {
    case Success(mbFetched) => {
      println(
        mbFetched some { "fetched: " + _.toString } none { "key does not exist" }).pure[IO]
    }
    case Failure(es) => {
      (es.foreach(e => println(e.getMessage))).pure[IO]
    }
  }

}

object Riak {

  implicit val BagConverter: ScaliakConverter[Bag] = ScaliakConverter.newConverter[Bag](
    (o: ReadObject) => new Bag(o.key, o.stringValue).successNel,
    (o: Bag) => WriteObject(o.key, o.value.getBytes))

}