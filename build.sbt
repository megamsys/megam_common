import net.virtualvoid.sbt.graph.Plugin
import org.scalastyle.sbt.ScalastylePlugin
import MegCommonReleaseSteps._
import sbtrelease._
import ReleaseStateTransformations._
import ReleasePlugin._
import ReleaseKeys._
import sbt._

name := "megam_common"

organization := "org.megam"

scalaVersion := "2.10.1"

scalacOptions := Seq("-unchecked", "-deprecation")

libraryDependencies ++= {
  val scalaCheckVersion = "1.10.1"
  val specs2Version = "1.14"
  val scalazVersion = "7.0.0"
  val zkVersion = "6.3.2"
  val amqpVersion = "3.0.4"
  val liftJsonVersion = "2.5-RC5"
  Seq(
    "org.scalaz" %% "scalaz-core" % scalazVersion,
    "org.scalaz" %% "scalaz-effect" % scalazVersion,
    "org.scalaz" %% "scalaz-concurrent" % scalazVersion,
    "net.liftweb" %% "lift-json-scalaz7" % liftJsonVersion,
    "com.twitter" % "util-zk-common" % zkVersion, 
    "com.rabbitmq" % "amqp-client" % amqpVersion,    
    "org.scalacheck" %% "scalacheck" % scalaCheckVersion % "test",
    "org.specs2" %% "specs2" % specs2Version % "test"    
  )
}

logBuffered := false

ScalastylePlugin.Settings

Plugin.graphSettings

releaseSettings

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  setReadmeReleaseVersion,
  tagRelease,
  publishArtifacts,
  setNextVersion,
  commitNextVersion,
  pushChanges
)

publishTo <<= (version) { version: String =>
  val nexus = "https://oss.sonatype.org/"
  if (version.trim.endsWith("SNAPSHOT")) {
    Some("snapshots" at nexus + "content/repositories/snapshots")
   } else {
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
  }
}

publishMavenStyle := true

publishArtifact in Test := true

testOptions in Test += Tests.Argument("html", "console")

pomIncludeRepository := { _ => false }

pomExtra := (
  <url>https://github.com/indykish/megam_common</url>
  <licenses>
    <license>
      <name>Apache 2</name>
      <url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>git@github.com:indykish/megam_common.git</url>
    <connection>scm:git:git@github.com:indykish/megam_common.git</connection>
  </scm>
  <developers>
    <developer>
      <id>indykish</id>
      <name>Kishorekumar Neelamegam</name>
      <url>http://www.megam.co</url>
    </developer>
    <developer>
      <id>rajthilakmca</id>
      <name>Raj Thilak</name>
      <url>http://www.megam.co</url>
    </developer>    
  </developers>
)
