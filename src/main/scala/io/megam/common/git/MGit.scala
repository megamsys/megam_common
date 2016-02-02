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
package io.megam.common.git

import scalaz._
import Scalaz._
import org.eclipse.jgit.api.Git
import scalaz.Validation.FlatMap._

object RepoName {
  def apply(name: String): RepoName = {
    val uriReg = """(http|https)\:\/\/(.*)\/(.*)\/(.*)""".r
    (name match {
      case uriReg(protocol, hostname, namespace, repo) =>
        RepoName(hostname, namespace, repo)
    })
  }
}

case class RepoName(
  hostname: String,
  namespace: String,
  repoWithPrefix: String) {

  val Array(repo, prefix) = repoWithPrefix.split('.')

  override def toString = {
    hostname + namespace + repo + prefix
  }
}

case class GitRepo(root: String, remote: String) {
  val name = RepoName(remote)

  val local = root + java.io.File.separator  + name.repo

}

object MGit {
  def clone(gr: GitRepo): ValidationNel[Throwable, GitRepo] = {
    (Validation.fromTryCatchThrowable[Git, Throwable] {
      Git.cloneRepository.setURI(gr.remote).setDirectory(new java.io.File(gr.local)).call
    } leftMap { t: Throwable => t }).toValidationNel.flatMap {
      g: Git => gr.successNel[Throwable]
    }
  }
}
