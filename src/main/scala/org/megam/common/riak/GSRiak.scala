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
import scalaz.EitherT._
import scalaz.Validation
import scalaz.NonEmptyList._
import com.stackmob.scaliak._
import com.basho.riak.client.query.indexes.{ RiakIndexes, IntIndex, BinIndex }
import com.basho.riak.client.cap.VClock
import org.slf4j.LoggerFactory
import org.megam.common._

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

  override def toString = {
    List("---->GUNNYSACK:", "key:" + key, "value:" + value, "contentType:" + contentType,
      (for (x <- metadata) yield (x)).mkString(" ", "Metadata:", ""),
      (for (x <- binIndexes) yield (x)).mkString(" ", "BinIndexes:", ""),
      (for (x <- metadata) yield (x)).mkString(" ", "IntIndexes:", ""),
      "vClock:" + vClock.getOrElse(""),
      "vTag:" + vTag,
      "lastModified:" + lastModified.toString).mkString("\n[", "\n", "]\n\n")
  }
}

/*
 * Any class that wants RiakOperations shall use the companion object and create one. 
 * eg: GSRiak("http://localhost:6999/riak", "firstbucket") 
 * The invoker shall provide a "bucketName". The uri will be pulled from the configuration
 * TO-DO: Change code to use types   [T](obj: T): IO[Validation[Throwable, Option[T]]] as opposed to 
 *                                                IO[Validation[Throwable, Option[GunnySack]] 
 **/
class GSRiak(uri: String, bucketName: String) {

  private lazy val logger = LoggerFactory.getLogger(getClass)

  import GSRiak._

  /**
   * Connect to the riak system using the scaliak client.
   */
  private lazy val client: ScaliakClient = Scaliak.httpClient(uri)

  /**
   * Do a ping. If an exception is thrown, then Riak connection doesn't exists.
   * The error is caught, and propagated to the composable chain.
   */
  lazy val ping = (Validation.fromTryCatch {
    client.ping
  } leftMap { t: Throwable =>
    GSConnectionError(uri)
  }).flatMap { i: Unit => Validation.success[Throwable, Option[String]](some("Connection to Riak successfully established using uri" + uri)) }

  /*
   * mkBucket creates a bucket when the ping returns a results stored in the Option[String]. 
   * If there is an error, a BucketCreationError is returned back.
   */
  private def mkBucket(res1: Option[String]): IO[Validation[Throwable, ScaliakBucket]] = res1 match {
    case Some(msg) => {
      logger.debug(msg)
      client.generateAndSetClientId()
      client.bucket(bucketName)
    }
    case None => {
      logger.debug("Ooops! Looks like you left me to wander when looking for %s bucket %s".format(uri, bucketName))
      Validation.failure[Throwable, ScaliakBucket](RiakError(nels(BucketCreateError(uri, bucketName)))).pure[IO]
    }
  }

  /*
   * bucketIO - This is a description of statements which when interpreted will result in a retrieving a bucket obejct using the 
   * bucketname. The "bucketName: String, value: ScaliakBucket are the input and output.
   * Merely calling this method doesn't return back a ScaliakBucket object. It just results in 
   * IO[x].
   */
  private def bucketIO: IO[Validation[Throwable, ScaliakBucket]] = {
    logger.debug("bucketIO:" + bucketName)

    (for {
      pingres <- eitherT[IO, Throwable, Option[String]] { // disjuction Throwable \/ Option with a Functor IO.   
        (ping.disjunction).pure[IO]
      }
      thatIO <- eitherT[IO, Throwable, ScaliakBucket] {
        mkBucket(pingres).map(_.disjunction)
      }
    } yield { thatIO }).run.map(_.validation)
  }

  /*
   * keysListIO - This is a description which when interpreted will result in a listsKeys operation of a bucket using a 
   * key. All the keys in a bucket are retrieved. Use it with caution, and only when you know that quantity of your bucket.
   * This return a Stream of strings.
   * Merely calling this method doesn't list all the keys in a listKeys operation. It just results in 
   * IO[x].
   */
  private def listKeysIO: IO[Validation[Throwable, Stream[String]]] = {
    logger.debug("listKeysIO:" + bucketName)

    bucketIO flatMap { mgBucket => //mgBucket is ValidationNel[Throwable, ScaliakBucket]
      mgBucket match {
        case Success(realMeat) => (realMeat.listKeys flatMap { x =>
          x match {
            case Success(res) => Validation.success[Throwable, Stream[String]](res).pure[IO]
            case Failure(err) => Validation.failure[Throwable, Stream[String]](RiakError(nels(err))).pure[IO]
          }
        })
        case Failure(nahNoBucket) => Validation.failure[Throwable, Stream[String]](RiakError(nels(BucketCreateError(uri, bucketName)))).pure[IO]
      }
    }
  }
  
  //List the all keys in bucket
  def listKeys: Validation[Throwable, Stream[String]] = listKeysIO.unsafePerformIO()

  /*
   * fetchIO - This is a description which when interpreted will result in a fetch operation of a bucket using a 
   * key. The "key : String, value: Option[GunnySack] are the input and output.
   * Merely calling this method doesn't fetch results in a fetch operation. It just results in 
   * IO[x].
   */
  private def fetchIO(key: String): IO[Validation[Throwable, Option[GunnySack]]] = {
    logger.debug("fetchIO:" + key)

    bucketIO flatMap { mgBucket => //mgBucket is ValidationNel[Throwable, ScaliakBucket]
      mgBucket match {
        case Success(realMeat) => (realMeat.fetch(key) flatMap { x =>
          x match {
            case Success(res) => Validation.success[Throwable, Option[GunnySack]](res).pure[IO]
            case Failure(err) => Validation.failure[Throwable, Option[GunnySack]](RiakError(err)).pure[IO]
          }
        })
        case Failure(nahNoBucket) => Validation.failure[Throwable, Option[GunnySack]](RiakError(nels(BucketFetchError(uri, bucketName, key)))).pure[IO]
      }
    }
  }

  //old code val fetchResult: ValidationNel[Throwable, Option[GunnySack]] = bucket.fetch(key).unsafePerformIO()
  def fetch(key: String) = fetchIO(key).unsafePerformIO.toValidationNel

  /*
   * fetchIndexIO - This is a description which when interpreted will result in a fetchIndexByValue operation of a bucket using a 
   * key. The "key : GunnySack, value: List[String] are the input and output.
   * Merely calling this method doesn't fetchIndex results in a fetchIndexByValue operation. It just results in 
   * IO[x].
   */
  private def fetchIndexIO(gs: GunnySack): IO[Validation[Throwable, List[String]]] = {
    logger.debug("fetchIndexIO:" + gs.toString)

    bucketIO flatMap { mgBucket => //mgBucket is ValidationNel[Throwable, ScaliakBucket]
      mgBucket match {
        case Success(realMeat) => (realMeat.fetchIndexByValue(gs.key + "_bin", gs.value) flatMap { x =>
          x.toValidationNel match {
            case Success(res) => Validation.success[Throwable, List[String]](res).pure[IO]
            case Failure(err) => Validation.failure[Throwable, List[String]](RiakError(err)).pure[IO]
          }
        })
        case Failure(nahNoBucket) => Validation.failure[Throwable, List[String]](RiakError(nels(BucketFetchError(uri, bucketName, gs.key)))).pure[IO]
      }
    }
  }

  // oldcode val indexVal = bucket.fetchIndexByValue(g.key + "_bin", g.value).unsafePerformIO()
  def fetchIndexByValue(gs: GunnySack) = fetchIndexIO(gs).unsafePerformIO.toValidationNel

  /*
   * storeIO - This is a description which when interpreted will result in a store operation of a bucket using a 
   * key/value. The "key:/value:" are inside the GunnySack. The value currently is a json representation as deemed fit and provided
   * by the callee, in this case the play model.
   * Merely calling this method doesn't store results in a store operation. It just results in 
   * IO[x].
   */
  private def storeIO[K, V](gs: GunnySack): IO[Validation[Throwable, Option[GunnySack]]] = {
    logger.debug("storeIO:" + gs.toString)

    bucketIO flatMap { mgBucket => //mgBucket is ValidationNel[Throwable, ScaliakBucket]
      mgBucket match {
        case Success(realMeat) => (realMeat.store(gs) flatMap { x =>
          x match {
            case Success(res) => Validation.success[Throwable, Option[GunnySack]](res).pure[IO]
            case Failure(err) => Validation.failure[Throwable, Option[GunnySack]](RiakError(err)).pure[IO]
          }
        })
        case Failure(nahNoBucket) => Validation.failure[Throwable, Option[GunnySack]](RiakError(nels(BucketStoreError(uri, bucketName, gs)))).pure[IO]
      }
    }
  }

  // oldcode val stored = bucket.store(gs).unsafePerformIO()
  def store(gs: GunnySack) = storeIO(gs).unsafePerformIO.toValidationNel

}

object GSRiak {

  implicit val GunnySackConverter: ScaliakConverter[GunnySack] = ScaliakConverter.newConverter[GunnySack](
    (o: ReadObject) => GunnySack(o.key, o.stringValue, o.contentType, o.links, o.metadata, o.binIndexes, o.intIndexes,
      o.vClock.some, o.vTag, o.lastModified).successNel,
    (o: GunnySack) => WriteObject(o.key, o.value.getBytes, o.contentType, o.links, o.metadata, o.vClock,
      o.vTag, o.binIndexes, o.intIndexes, o.lastModified))

  def apply(uri: String, bucketName: String) = new GSRiak(uri, bucketName)

}
