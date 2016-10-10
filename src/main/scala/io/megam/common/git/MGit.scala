/*
** Copyright [2013-2016] [Megam Systems]
**
** https://opensource.org/licenses/MIT
**
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
