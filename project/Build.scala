/**
 * Copyright 2012-2013 Megam Systems
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import io.Source
import java.io.PrintWriter
import sbtrelease._
import ReleasePlugin._
import ReleaseKeys._
import sbt._
import Keys._
import Utilities._
import com.typesafe.sbt.SbtPgp.PgpKeys._

object MegCommonReleaseSteps {

  val readme = "README.md"

  lazy val setReadmeReleaseVersion: ReleaseStep = { st: State =>
    val releaseVersions = getReleasedVersion(st)
    updateReadme(st, releaseVersions._1)
    commitReadme(st, releaseVersions._1)
    st
  }

  lazy val publishSignedAction: ReleaseStep = { st: State =>
    val extracted = st.extract
    val ref = extracted.get(thisProjectRef)
    extracted.runAggregated(publishSigned in Global in ref, st)
  }
  
  private def getReleasedVersion(st: State): (String, String) = {
    st.get(versions).getOrElse(sys.error("No versions are set."))
  }

  private def updateReadme(st: State, newVersion: String) {
    val newmanRegex = """\d+\.\d+\.\d+""".r
    val oldReadme = Source.fromFile(readme).mkString
    val out = new PrintWriter(readme, "UTF-8")
    try {
      val newReadme = newmanRegex.replaceAllIn(oldReadme, "%s".format(newVersion))
      newReadme.foreach(out.write(_))
    } finally {
      out.close()
    }
  }

  private def commitReadme(st: State, newVersion: String) {
    val vcs = Project.extract(st).get(versionControlSystem).getOrElse(sys.error("Unable to get version control system."))
    vcs.add(readme) !! st.log
    vcs.commit("README.md updated to %s".format(newVersion)) ! st.log
  }

}
