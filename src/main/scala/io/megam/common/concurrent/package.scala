/*
** Copyright [2013-2016] [Megam Systems]
**
** https://opensource.org/licenses/MIT
**
*/
package io.megam.common

import scala.concurrent.duration._
import scala.concurrent._
import scalaz.Validation
/**
 * @author ram
 *
 */
package object concurrent {

  implicit val duration = 5.seconds

  implicit val SequentialExecutionContext: ExecutionContext = new ExecutionContext {
    def execute(runnable: Runnable) {
      runnable.run()
    }
    def reportFailure(t: Throwable) {}
    override lazy val prepare: ExecutionContext = this
  }

  implicit class RichFuture[T](fut: Future[T]) {
    def toEither(dur: Duration = duration): Either[Throwable, T] = {
      Validation.fromTryCatchThrowable[T,Throwable](block(dur)).toEither
    }

    def block(dur: Duration = duration): T = {
      Await.result(fut, dur)
    }
  }

}
