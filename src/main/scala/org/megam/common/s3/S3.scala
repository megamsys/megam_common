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
package org.megam.common.s3

import scalaz._
import Scalaz._
import scalaz.effect.IO
import scalaz.EitherT._
import scalaz.Validation
import scalaz.Validation.FlatMap._
import scalaz.NonEmptyList._
import scala.collection.JavaConverters._
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.{ AmazonS3, AmazonS3Client }
import com.amazonaws.services.s3.model._
import org.slf4j.LoggerFactory
import java.io.File
import org.megam.common._
import org.megam.common.s3._
import org.megam.common.s3.S3Errors._

class S3(credent: Credentials) {

  private lazy val logger = LoggerFactory.getLogger(getClass)

  private lazy val credentials = new BasicAWSCredentials(credent._1, credent._2)

  val connection: Validation[Throwable, AmazonS3Client] = (Validation.fromTryCatchThrowable[AmazonS3Client,Throwable] {
    new AmazonS3Client(credentials)
  } leftMap { t: Throwable => S3ConnectionError(credent) })

  def objectListing(bucketName: String, vl: String): Validation[Throwable, ObjectListing] = {
    val res = (for {
      conn <- connection
    } yield {
      //conn.setEndpoint(region)
      conn.listObjects(new ListObjectsRequest().withBucketName(bucketName).withPrefix(vl))
    }) leftMap { t: Throwable => S3Error(nels(ListingError(bucketName, vl))) }
    //res.getOrElse(Validation.failure[Throwable, ObjectListing](S3Error(nels(ListingError(accessKey, secretKey)))))
    res
  }

  def download(bucketName: String, vl: String): Validation[Throwable, scala.collection.mutable.Buffer[Any]] = {
    val res = (for {
      conn <- connection
      ol <- objectListing(bucketName, vl)
    } yield {
      ol.getObjectSummaries.asScala.map(objectSummary =>
        if (objectSummary.getSize() > 0) {
          conn.getObject(new GetObjectRequest(bucketName,
            objectSummary.getKey()), new File(
            new java.io.File(".").getCanonicalPath + "/" + bucketName + "/" + objectSummary.getKey()))
        })
    }) leftMap { t: Throwable => S3Error(nels(DownloadError(vl))) }
    // res.getOrElse(Validation.failure[Throwable, scala.collection.mutable.Buffer[Any]](S3Error(nels(DownloadError(vl)))))
    res
  }

}

object S3 {
  def apply(credent: Credentials) = new S3(credent)
}

