/*
** Copyright [2013-2016] [Megam Systems]
**
** https://opensource.org/licenses/MIT
**
*/
package io.megam.auth.funnel

import scalaz._
import Scalaz._
import java.io.{ StringWriter, PrintWriter }
import io.megam.common.jsonscalaz._
import net.liftweb.json._
import net.liftweb.json.scalaz.JsonScalaz._
import play.api.http.Status._

/**
 * @author ram
 *
 */
object FunnelErrors {

  val tailMsg =
    """Forum   :https://forumns.megam.io
  	  |Docs    :https://docs.megam.io""".stripMargin

  case class CannotAuthenticateError(input: String, msg: String, httpCode: Int = UNAUTHORIZED)
    extends java.lang.Error(msg)

  case class MalformedBodyError(input: String, msg: String, httpCode: Int = BAD_REQUEST)
    extends java.lang.Error(msg)

 case class PermissionNotThere(input: String, msg: String, httpCode: Int = FORBIDDEN)
    extends java.lang.Error(msg)

  case class MalformedHeaderError(input: String, msg: String, httpCode: Int = NOT_ACCEPTABLE)
    extends java.lang.Error(msg)

  case class ServiceUnavailableError(input: String, msg: String, httpCode: Int = SERVICE_UNAVAILABLE)
    extends java.lang.Error(msg)

  case class ResourceItemNotFound(input: String, msg: String, httpCode: Int = NOT_FOUND)
    extends java.lang.Error(msg)

  case class JSONParsingError(errNel: NonEmptyList[net.liftweb.json.scalaz.JsonScalaz.Error])
  extends java.lang.Error({
    errNel.map { err: net.liftweb.json.scalaz.JsonScalaz.Error =>
      err.fold(
        u => "unexpected JSON %s. expected %s".format(u.was.toString, u.expected.getCanonicalName),
        n => "no such field %s in json %s".format(n.name, n.json.toString),
        u => "uncategorized error %s while trying to decode JSON: %s".format(u.key, u.desc))
    }.list.mkString("\n")
  })

  case class HttpReturningError(errNel: NonEmptyList[Throwable]) extends Exception {

    def mkMsg(err: Throwable): String = {
      err.fold(
        a => """Authentication failure using the email/apikey combination. %n'%s'
            |verify the email and api key combination.
            """.format(a.input).stripMargin,
        m => """Body received contains invalid input. 'body:' %n'%s'
            |verify the body content as needed for this resource.
            |""".format(m.input).stripMargin,
        h => """Header received contains invalid input. 'header:' %n'%s'
            |verify the header content as required for this resource.
            |%s""".format(h.input).stripMargin,
        c => """Service layer failed to perform the request
            |verify cassandra or nsq %n'%s'""".format(c.input).stripMargin,
        r => """The resource wasn't found   '%s'""".format(r.input).stripMargin,
        f => """'admin' authority required to access this resource  '%s'""".format(f.input).stripMargin,
        t => """Ooops ! I know its crazy. We flunked.
            |Contact support with this text.
            """.format(t.getLocalizedMessage).stripMargin)
    }

    def msg: String = {
      errNel.map { err: Throwable => mkMsg(err) }.list.mkString("\n")
    }

    def mkCode(err: Throwable): Option[Int] = {
      err.fold(a => a.httpCode.some, m => m.httpCode.some, h => h.httpCode.some, c => c.httpCode.some,
        r => r.httpCode.some, f => f.httpCode.some, t => INTERNAL_SERVER_ERROR.some)

    }

    def code: Option[Int] = { (errNel.map { err: Throwable => mkCode(err) }.list.head) }

    def mkMore(err: Throwable) = {
      err.fold(a => null,
        m => """|The error received when parsing the json is :
    		  	|%s""".format(m.msg).stripMargin,
        h => null,
        c => """|The error received from the service :
    		  	|%s""".format(c.msg).stripMargin,
        r => """|The error received from the datasource :
    		  	|%s""".format(r.msg).stripMargin,
        f => null,
        t => """|Pardon us. This is how it happened.
            |Stack trace
            |%s
            """.format({ val u = new StringWriter; t.printStackTrace(new PrintWriter(u)); u.toString }).stripMargin)
    }

    def more: Option[String] = { errNel.map { err: Throwable => mkMore(err) }.list.mkString("\n").some }

    def severity = { "error" }

  }

}
