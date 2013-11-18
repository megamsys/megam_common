import net.virtualvoid.sbt.graph.Plugin
import org.scalastyle.sbt.ScalastylePlugin
import MegCommonReleaseSteps._
import sbtrelease._
import ReleaseStateTransformations._
import ReleasePlugin._
import ReleaseKeys._
import sbt._

name := "megam_common"

organization := "com.github.indykish"

scalaVersion := "2.10.3"

scalacOptions := Seq(
	"-unchecked", 
	"-deprecation",
	"-feature",
 	"-optimise",
 	"-explaintypes",
  	"-Xcheckinit",
  	"-Xlint",
  	"-Xverify",  	
  	"-Yinline-warnings",
  	"-Ywarn-all",
  	"-Yclosure-elim",
  	"-language:postfixOps",
  	"-language:implicitConversions",
  	"-Ydead-code")

resolvers += "Typesafe Snapshots" at "http://repo.typesafe.com/typesafe/snapshots"

resolvers  +=  "Sonatype OSS Snapshots"  at  "https://oss.sonatype.org/content/repositories/snapshots"

resolvers  += "Scala-Tools Maven2 Snapshots Repository" at "http://scala-tools.org/repo-snapshots"

resolvers += "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases"

resolvers += "Sonatype Releases" at "https://oss.sonatype.org/content/public"
      
resolvers += "Twitter Repo" at "http://maven.twttr.com"   
       
libraryDependencies ++= {
  val scalazVersion = "7.0.4"
  val liftJsonVersion = "2.5.1"
  val zkVersion = "6.8.1"
  val amqpVersion = "3.2.1"
  val scalaCheckVersion = "1.10.1"
  val specs2Version = "2.3.3"  
  Seq(
    "org.scalaz" %% "scalaz-core" % scalazVersion,
    "net.liftweb" %% "lift-json-scalaz7" % liftJsonVersion,
    "com.stackmob" % "scaliak_2.10" % "0.9.0",
    "com.twitter" % "util-zk_2.10" % zkVersion,
    "com.twitter" % "util-zk-common_2.10" % zkVersion,
    "com.rabbitmq" % "amqp-client" % amqpVersion,    
    "org.scalacheck" %% "scalacheck" % scalaCheckVersion % "test",
    "org.specs2" %% "specs2" % specs2Version % "test",   
    "org.apache.thrift" % "libthrift" % "0.5.0",
    "com.twitter.service" % "snowflake" % "1.0.2" from "https://s3-ap-southeast-1.amazonaws.com/megampub/0.1/jars/snowflake.jar"
    )
}

//conflictManager := ConflictManager.strict

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
  publishArtifacts.copy(action = publishSignedAction),
  setNextVersion,
  commitNextVersion,
  pushChanges
)

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (version.value.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

publishMavenStyle := true

publishArtifact in Test := false

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
