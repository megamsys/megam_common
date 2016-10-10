/*
** Copyright [2013-2016] [Megam Systems]
**
** https://opensource.org/licenses/MIT
**
*/
package io.megam.auth.stack

import scalaz._
import Scalaz._
import scalaz.Validation._
import scala.concurrent.Future

import jp.t2v.lab.play2.stackc.{ RequestWithAttributes, RequestAttributeKey, StackableController }

import io.megam.common.Constants._
import io.megam.auth.funnel._
import io.megam.auth.funnel.FunnelErrors._
import play.api.mvc._
import play.api.libs.iteratee.Enumerator

/**
 * @author rajthilak
 *
 */
/*
 * sub trait for stackable controller, proceed method was override here for our request changes,
 * And result return in super trait proceed method,
 * when stack action is called then this stackable controller is executed
 */
trait AuthElement extends StackableController with RequestAttributeKeyConstants {

  self: Controller =>

  def authImpl(input: String): ValidationNel[Throwable, Option[AccountResult]]

  def masterImpl(input: String): ValidationNel[Throwable, Option[MasterKeyResult]]

  /**
   * If HMAC authentication is true, the req send in super class
   * otherwise send out a json formatted error
   */
  override def proceed[A](req: RequestWithAttributes[A])(f: RequestWithAttributes[A] => Future[Result]): Future[Result] = {
    play.api.Logger.debug("%s%s====> %s%s%s ".format(Console.CYAN, Console.BOLD, req.host, req.path, Console.RESET))
    play.api.Logger.debug("%s%sHEAD:%s %s%s%s".format(Console.MAGENTA, Console.BOLD, Console.RESET, Console.BLUE, req.headers, Console.RESET))
    play.api.Logger.debug("%s%sBODY:%s %s%s%s\n".format(Console.MAGENTA, Console.BOLD, Console.RESET, Console.BLUE, req.body, Console.RESET))
    implicit val (r, ctx) = (req, StackActionExecutionContext(req))

    SecurityActions.Authenticated(req, authImpl, masterImpl) match {
      case Success(rawRes) => {
        play.api.Logger.debug("%s%sAUTHBAG:%s %s%s%s\n".format(Console.YELLOW, Console.BOLD, Console.RESET, Console.BLUE, rawRes, Console.RESET))
        val ro = req.set(APIKey, rawRes)
        play.api.Logger.debug("%s%sAUTHBAG0:%s %s%s%s\n".format(Console.YELLOW, Console.BOLD, Console.RESET, Console.BLUE, ro, Console.RESET))

        play.api.Logger.debug("%s%sAUTHBAG1:%s %s%s%s\n".format(Console.YELLOW, Console.BOLD, Console.RESET, Console.BLUE, req.get(APIKey).get, Console.RESET))
        super.proceed(ro)(f)
      }
      case Failure(err) => {
        val g = Action { implicit request =>
          val rn: FunnelResponse = new HttpReturningError(err) //implicitly loaded.
          Result(header = ResponseHeader(rn.code, Map(CONTENT_TYPE -> "text/plain")),
            body = Enumerator(rn.toJson(true).getBytes(UTF8Charset)))
        }
        val origReq = req.asInstanceOf[Request[AnyContent]]
        g(origReq)
      }

    }
  }

  implicit def reqFunneled[A](implicit req: RequestWithAttributes[A]): ValidationNel[Throwable, Option[FunneledRequest]] = req2FunnelBuilder(req).funneled

  implicit def grabAuthBag[A](implicit req: RequestWithAttributes[A]): Option[AuthBag] = req.get(APIKey).get

}
