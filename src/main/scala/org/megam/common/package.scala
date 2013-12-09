package org.megam

import java.nio.charset.Charset
import scalaz._
import scalaz.effect.IO
import scalaz.NonEmptyList._
import Scalaz._
import org.apache.zookeeper.ZooDefs.Ids
import org.apache.zookeeper._
import org.apache.zookeeper.CreateMode
import org.apache.zookeeper.data.ACL
import org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE
import scala.collection.JavaConverters._
import com.twitter.util.Stopwatch
import org.megam.common.riak.GSErrors._
import org.megam.common.s3.S3Errors._

package object common {

  implicit class RichThrowable(thrownExp: Throwable) {
    def fold[T](connError: GSConnectionError => T,
      buckCreateError: BucketCreateError => T,
      fetchBuckError: BucketFetchError => T,
      storeError: BucketStoreError => T,
      anyError: Throwable => T): T = thrownExp match {
      case c @ GSConnectionError(_)      => connError(c)
      case b @ BucketCreateError(_, _)   => buckCreateError(b)
      case f @ BucketFetchError(_, _, _) => fetchBuckError(f)
      case s @ BucketStoreError(_, _, _) => storeError(s)
      case t @ _                         => anyError(t)
    }

    def fold[T](connError: S3ConnectionError => T,
      downError: DownloadError => T,
      listError: ListingError => T,
      anyError: Throwable => T): T = thrownExp match {
      case c @ S3ConnectionError(_) => connError(c)
      case f @ DownloadError(_)     => downError(f)
      case l @ ListingError(_, _)   => listError(l)
      case t @ _                    => anyError(t)
    }
  }

  type ZooPath = String

  val DefaultACL: Seq[ACL] = OPEN_ACL_UNSAFE.asScala

  val DefaultMode: CreateMode = CreateMode.PERSISTENT
  //val DefaultMode: CreateMode = CreateMode.EPHEMERAL_SEQUENTIAL

  val ZooRootPath = "/machines"

  val TwitStopwatch = com.twitter.util.Stopwatch

}