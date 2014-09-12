import net.virtualvoid.sbt.graph.Plugin
import org.scalastyle.sbt.ScalastylePlugin
import MegCommonReleaseSteps._
import sbtrelease._
import ReleaseStateTransformations._
import ReleasePlugin._
import ReleaseKeys._
import sbt._
import com.github.bigtoast.sbtthrift.ThriftPlugin

name := "megam_common"

organization := "com.github.indykish"

scalaVersion := "2.10.4"

crossScalaVersions := Seq("2.10.4", "2.11.2")

scalacOptions := Seq(
  "-target:jvm-1.7",
  "-deprecation",
  "-feature",
  "-optimise",
  "-Xcheckinit",
  "-Xlint",
  "-Xverify",
  "-Yinline",
  "-Yclosure-elim",
  //"-Yconst-opt", 
  //"-Ybackend:GenBCode",
  //"closurify:delegating",
  "-language:implicitConversions",
  "-language:higherKinds",
  "-language:reflectiveCalls",
  "-language:postfixOps",
  "-language:implicitConversions",
  "-Ydead-code")

incOptions := incOptions.value.withNameHashing(true)

resolvers += "Twitter Repo" at "http://maven.twttr.com"

resolvers += "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases"

resolvers += "Sonatype Releases" at "https://oss.sonatype.org/content/public"

resolvers += "Typesafe Snapshots" at "http://repo.typesafe.com/typesafe/snapshots"

resolvers  +=  "Sonatype Snapshots"  at  "https://oss.sonatype.org/content/repositories/snapshots"

resolvers  += "Scala-Tools Maven2 Snapshots Repository" at "http://scala-tools.org/repo-snapshots"

resolvers += "JBoss" at "https://repository.jboss.org/nexus/content/groups/public"

libraryDependencies ++= {
  val scalazVersion = "7.0.6"
  val liftJsonVersion = "3.0-M1"
  val amqpVersion = "3.3.5"
  val specs2Version = "2.4.2-scalaz-7.0.6"
  Seq(
    "org.scalaz" %% "scalaz-core" % scalazVersion,
    "org.scalaz" %% "scalaz-iteratee" % scalazVersion,
    "org.scalaz" %% "scalaz-effect" % scalazVersion,
    "org.scalaz" %% "scalaz-concurrent" % scalazVersion % "test",
    "net.liftweb" %% "lift-json-scalaz7" % liftJsonVersion,
    "com.basho.riak" % "riak-client" % "2.0.0.RC1",
    "org.apache.commons" % "commons-pool2" % "2.2",
    "org.slf4j" % "slf4j-api" % "1.7.7",
    //"com.stackmob" %% "scaliak" % "0.10.0-SNAPSHOT",
    "com.stackmob" %% "scaliak" % "0.10.0-SNAPSHOT" from "https://s3-ap-southeast-1.amazonaws.com/megampub/0.5/jars/scaliak_2.10-0.10.0-SNAPSHOT.jar",
    "com.rabbitmq" % "amqp-client" % amqpVersion,
    "org.specs2" %% "specs2" % specs2Version % "test",    
    "org.apache.thrift" % "libthrift" % "0.9.1" excludeAll (
      ExclusionRule("commons-logging", "commons-logging"),
      ExclusionRule("org.slf4j","slf4j-simple"),
      ExclusionRule("org.slf4j","slf4j-nop"),
      ExclusionRule("org.slf4j", "slf4j-jdk14")),
    "org.apache.commons" % "commons-lang3" % "3.3.2",      
    "com.amazonaws" % "aws-java-sdk" % "1.8.9.1"
    )
}

logBuffered := false

// isSnapshot := true

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

seq(ThriftPlugin.thriftSettings: _*)

publishTo in ThisBuild            <<= isSnapshot(if (_) Some(Opts.resolver.sonatypeSnapshots) else Some(Opts.resolver.sonatypeStaging))

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
    <url>git@github.com:megamsys/megam_common.git</url>
    <connection>scm:git:git@github.com:megamsys/megam_common.git</connection>
  </scm>
  <developers>
    <developer>
      <id>indykish</id>
      <name>Kishorekumar Neelamegam</name>
      <url>http://www.gomegam.com</url>
    </developer>
    <developer>
      <id>rajthilakmca</id>
      <name>Raj Thilak</name>
      <url>http://www.gomegam.com</url>
    </developer>
  </developers>
)
