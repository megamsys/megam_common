package org.megam

import java.nio.charset.Charset
import scalaz._
import scalaz.effect.IO
import scalaz.NonEmptyList._
import Scalaz._

import scala.collection.JavaConverters._
import org.megam.util.Stopwatch


import org.megam.common.riak.GSErrors._

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

  }

  

}
