/*
** Copyright [2012] [Megam Systems]
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
/**
 * @author rajthilak
 *
 */
import io.megam.common.git._
import org.specs2._
import scalaz._
import Scalaz._
import org.specs2.mutable._
import org.specs2.Specification
import org.specs2.matcher.MatchResult

class GitCloneSpecs extends Specification {

  def is =
    "GitCloneSpecs".title ^ end ^
      """
  Git clone which interfaces with git
    """ ^ end ^
      "The Git clone spec Should" ^
      "Correctly get reponame for a valid git repo " ! GitRepoName.succeeds ^
      "Correctly clone for a valid git repo " ! GitCloneFetch.succeeds ^
      end

  case object GitRepoName {

    def succeeds = {
      val r = new GitRepo("/home/ram/Desktop/first", "https://github.com/megamsys/meghack.git")
      r.name.hostname must beEqualTo("github.com")
      r.name.namespace must beEqualTo("megamsys")
      r.name.repo must beEqualTo("meghack")
      r.name.prefix must beEqualTo("git")
      r.local must beEqualTo("/home/ram/Desktop/first/meghack")

    }
  }

  case object GitCloneFetch {
    def succeeds = {
      val t: ValidationNel[Throwable, GitRepo] = MGit.clone(new GitRepo("/home/ram/code/megam/home/megamgateway/yonpis", "https://github.com/megamsys/testsparkbb.git"))
      t.toOption must beSome
    }
  }
}
