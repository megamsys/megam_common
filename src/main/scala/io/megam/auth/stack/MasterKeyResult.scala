/*
** Copyright [2013-2016] [Megam Systems]
**
** https://opensource.org/licenses/MIT
**
*/
package io.megam.auth.stack

import scalaz._
import Scalaz._
import scalaz.Validation
import scalaz.Validation.FlatMap._
import scalaz.NonEmptyList._
import scala.concurrent.Future

import io.megam.common.Constants._
import io.megam.auth.funnel._
import io.megam.auth.funnel.FunnelErrors._

import io.megam.util.Time
import net.liftweb.json._
import net.liftweb.json.scalaz.JsonScalaz._
import java.nio.charset.Charset

case class MasterKeyResult(id: String, key: String, created_at: String) {}
