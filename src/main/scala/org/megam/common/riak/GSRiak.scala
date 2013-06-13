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
import com.basho.riak.client.query.indexes.{RiakIndexes, IntIndex, BinIndex}
/**
 * @author ram
 *
 */
case class GunnySack(key: String, value: String)

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

  /*
   * store the specified key and their value to riak bucket
   */
  def store[K, V](key: String, value: String): ValidationNel[Throwable, Option[GunnySack]] = {
    val stored = bucket.store(new GunnySack(key, value)).unsafePerformIO()
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

  def storeWithIndex(key: String, value: String) = {
    val existingMetadataKey = "Field2_int"
     val existingMetadataVal = "1002"     
    
       
    val bindex = BinIndex.named("email")
      val bvalue = Set("val5")
     val testwriteObject = WriteObject(
    "mykey12",
    "".getBytes, "",
    null,
    metadata = Map(existingMetadataKey -> existingMetadataVal),
    binIndexes = Map((bindex, bvalue))
    )
    
     val testObject = ReadObject(
    "mykey12",
    "", "",
    null, "".getBytes,
    metadata = Map(existingMetadataKey -> existingMetadataVal),
    binIndexes = Map((bindex, bvalue))
    )
    val value2 = testObject.addMetadata("a", "b")
    val value3 = testObject.binIndex("Field7_bin")
    
    println("write object"+testwriteObject._binIndexes)
       println("write object"+testwriteObject._metadata)
     println("=====================================>"+value2)
    println("--------------------"+value3)
    val value4 = fetchIndexByValue("val5")
     println("--------------------"+value4)
     println("++++++++"+bindex.getName())
  }
  
  def fetchIndexByValue(email: String): Validation[Throwable, List[String]] = {
     val valueI: Validation[Throwable, List[String]] = bucket.fetchIndexByValue("email_bin", email).unsafePerformIO()   
     println(valueI)
     valueI
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
    (o: ReadObject) => new GunnySack(o.key, o.stringValue).successNel,
    (o: GunnySack) => WriteObject(o.key, o.value.getBytes))

  def apply(uri: String, bucketName: String) = new GSRiak(uri, bucketName)

}