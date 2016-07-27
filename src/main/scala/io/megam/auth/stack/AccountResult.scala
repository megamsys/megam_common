/*
** Copyright [2013-2016] [Megam Systems]
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



case class Name(first_name: String, last_name: String) {
val json = "{\"first_name\":\"" + first_name + "\",\"last_name\":\"" + last_name + "\"}"
 }

 object Name {
   def empty: Name = new Name(new String(), new String())
 }

 case class Phone(phone: String, phone_verified: Boolean) {
val json = "{\"phone\":\"" + phone + "\",\"phone_verified\":"+ phone_verified + "}"

 }
 object Phone {
   def empty: Phone = new Phone(new String(), false)
 }

 case class Password(password: String, password_reset_key: String, password_reset_sent_at: String) {
 val json = "{\"password\":\"" + password + "\",\"password_reset_key\":\"" + password_reset_key + "\",\"password_reset_sent_at\":\"" + password_reset_sent_at + "\"}"
}

object Password {
def apply(password_reset_key: String, password_reset_sent_at: String): Password = Password(new String(), new String())
  def empty: Password = new Password(new String(), new String(), new String())
}

case class Approval(approved: Boolean, approved_by_id: String, approved_at: String) {
val json = "{\"approved\":" + approved + ",\"approved_by_id\":\"" + approved_by_id + "\",\"approved_at\":\"" + approved_at + "\"}"
}
object Approval {
  def empty: Approval = new Approval(false, new String(), new String())
}

case class Suspend(suspended: Boolean, suspended_at: String, suspended_till: Boolean) {
val json = "{\"suspended\":" + suspended + ",\"suspended_at\":\"" + suspended_at + "\",\"suspended_till\":" + suspended_till + "}"

}
object Suspend {
  def empty: Suspend = new Suspend(false, new String(), false)
}

case class Dates(last_posted_at: String, last_emailed_at: String, previous_visit_at: String, first_seen_at: String, created_at: String) {
val json = "{\"last_posted_at\":\"" + last_posted_at + "\",\"last_emailed_at\":\"" + last_emailed_at + "\",\"previous_visit_at\":\"" + previous_visit_at + "\",\"first_seen_at\":\"" + first_seen_at + "\", \"created_at\":\""+created_at+"\"}"
}
object Dates {
  def empty: Dates = new Dates(new String(), new String(), new String(), new String(), new String())
}

case class States(authority: String, active: Boolean, blocked: Boolean, staged: String) {
val json = "{\"authority\":\"" + authority + "\",\"active\":" + active + ",\"blocked\":" + blocked + ", \"staged\":\"" + staged + "\"}"

}
object States {
  def empty: States = new States(new String(), false, false, new String() )
}


case class AccountResult(id: String, name: Name,  phone: Phone, email: String, api_key: String, password: Password, states: States,  approval: Approval, suspend: Suspend, registration_ip_address: String, dates: Dates) {
/*
  def toJValue: JValue = {
    import net.liftweb.json.scalaz.JsonScalaz.toJSON
    import io.megam.json.AccountResultSerialization
    val acctser = new AccountResultSerialization()
    toJSON(this)(acctser.writer)
  }

  def toJson(prettyPrint: Boolean = false): String = if (prettyPrint) {
    prettyRender(toJValue)
  } else {
    compactRender(toJValue)
  }
*/
}

object AccountResult {

  //def apply(id: String, email: String, api_key: String, authority: String) = new AccountResult(id, email, api_key, authority, Time.now.toString)
  //def apply(id: String, name: Name,  phone: Phone, email: String, api_key: String, password: Password, states: States,  approval: Approval, suspend: Suspend, registration_ip_address: String, dates: Dates) = new AccountResult(id, name, phone, email, api_key,  password,  states,  approval, suspend, registration_ip_address, dates)

  def apply(email: String): AccountResult = AccountResult("not found", Name.empty, Phone.empty,  email, new String(), Password.empty, States.empty, Approval.empty, Suspend.empty, new String(), Dates.empty)
/*
  def fromJValue(jValue: JValue)(implicit charset: Charset = UTF8Charset): Result[AccountResult] = {
    import net.liftweb.json.scalaz.JsonScalaz.fromJSON
    import io.megam.json.AccountResultSerialization
    val acctser = new AccountResultSerialization()
    fromJSON(jValue)(acctser.reader)
  }

  def fromJson(json: String): Result[AccountResult] = (Validation.fromTryCatchThrowable[net.liftweb.json.JValue,Throwable] {
    parse(json)
  } leftMap { t: Throwable =>
    UncategorizedError(t.getClass.getCanonicalName, t.getMessage, List())
  }).toValidationNel.flatMap { j: JValue => fromJValue(j) }
*/
}
