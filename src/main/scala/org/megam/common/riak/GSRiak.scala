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
import com.basho.riak.client.query.indexes.{ RiakIndexes, IntIndex, BinIndex }
import com.basho.riak.client.cap.VClock
/**
 * @author ram
 *
 */
import com.basho.riak.client.http.util.{ Constants => RiakConstants }

case class GunnySack(key: String, value: String, contentType: String = RiakConstants.CTYPE_TEXT_UTF8,
  links: Option[NonEmptyList[ScaliakLink]] = none, metadata: Map[String, String] = Map(),
  binIndexes: Map[BinIndex, Set[String]] = Map(), intIndexes: Map[IntIndex, Set[Int]] = Map(),
  vClock: Option[VClock] = none, vTag: String = "",
  lastModified: java.util.Date = new java.util.Date(System.currentTimeMillis)) {

}

/*
 * Any class that wants RiakOperations shall implement this trait. 
 * The invoker shall provide a "bucketName". The uri will be pulled from the configuration
 */
class GSRiak(uri: String, bucketName: String) {

  import GSRiak._

  /**
   * Connect to the riak system using the scaliak client.
   */
  private lazy val client: ScaliakClient = Scaliak.httpClient(uri)

  /*
   * connect the existing bucket in riak client
   * if doesn't bucket then, create a new bucket
   */
  private lazy val bucket: ScaliakBucket = {
    client.generateAndSetClientId()
    val bucket = client.bucket(bucketName).unsafePerformIO() match {
      case Success(b) => b
      case Failure(e) => throw e
    }
    bucket
  }

  //do a dummy ping. If an exception is thrown, then Riak connection doesn't exists. 
  val ping = client.ping
  
  //When the client starts 
  val bucketsList = client.listBuckets.unsafePerformIO
  
  //List the all keys in bucket
  val keysList = bucket.listKeys.unsafePerformIO()
  /*
   * store the specified key and their value to riak bucket
   */
  def store[K, V](gs: GunnySack): ValidationNel[Throwable, Option[GunnySack]] = {
    //def store(gs: GunnySack): ValidationNel[Throwable, Option[GunnySack]] = {
    val stored = bucket.store(gs).unsafePerformIO()
    stored
  }

  /*
   * fetch a Bag object
   * and return the option node object
   */
  def fetch(key: String): ValidationNel[Throwable, Option[GunnySack]] = {
    val fetchResult: ValidationNel[Throwable, Option[GunnySack]] = bucket.fetch(key).unsafePerformIO()
    fetchResult
  }   
  
  def fetchIndexByValue(g: GunnySack): ValidationNel[Throwable, List[String]] = {
    val indexVal = bucket.fetchIndexByValue(g.key + "_bin", g.value).unsafePerformIO()
    println(indexVal)
    indexVal.toValidationNel
  }

  private def printFetchRes(v: ValidationNel[Throwable, Option[GunnySack]]): IO[Unit] = v match {
    case Success(mbFetched) => {
      println(
        mbFetched some { "fetched: " + _.toString } none { "key does not exist" }).pure[IO]
    }
    case Failure(es) => {
      (es.foreach(e => println(e.getMessage))).pure[IO]
    }
  }

}

object GSRiak {

  implicit val GunnySackConverter: ScaliakConverter[GunnySack] = ScaliakConverter.newConverter[GunnySack](
    (o: ReadObject) => GunnySack(o.key, o.stringValue, o.contentType, o.links, o.metadata, o.binIndexes, o.intIndexes,
      o.vClock.some, o.vTag, o.lastModified).successNel,
    (o: GunnySack) => WriteObject(o.key, o.value.getBytes, o.contentType, o.links, o.metadata, o.vClock,
      o.vTag, o.binIndexes, o.intIndexes, o.lastModified))

  def apply(uri: String, bucketName: String) = new GSRiak(uri, bucketName)

}