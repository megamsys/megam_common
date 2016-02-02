import sbt._
import sbt.Keys._

name := "libcommon"

organization := "io.megam"

description := """This is a set of function libraries used in megam.io. This contains amqp, json, riak and an unique id thrift client based on snowflake all built using a funcitonal twist.
Feel free to collaborate at https://github.com/megamsys/megam_common.git."""

licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html"))

scalaVersion := "2.11.7"

bintrayOrganization := Some("megamsys")

bintrayRepository := "scala"

publishMavenStyle := true

scalacOptions := Seq(
	  "-target:jvm-1.8",
		"-deprecation",
	  "-feature",
	  "-optimise",
	  "-Xcheckinit",
	  "-Xlint",
	  "-Xverify",
	  "-Yinline",
	  "-Yclosure-elim",
	  "-Yconst-opt",
	  "-Ybackend:GenBCode",
	  "-language:implicitConversions",
	  "-language:higherKinds",
	  "-language:reflectiveCalls",
	  "-language:postfixOps",
	  "-language:implicitConversions",
	  "-Ydead-code")

scalacOptions in Test ++= Seq("-Yrangepos")

incOptions := incOptions.value.withNameHashing(true)

resolvers ++= Seq(Resolver.sonatypeRepo("releases"),
Resolver.sonatypeRepo("snapshots"),
Resolver.bintrayRepo("scalaz", "releases"),
Resolver.bintrayRepo("io.megam", "scala")
)

{
  val scalazVersion = "7.1.6"
  val liftJsonVersion = "3.0-M8"
  val amqpVersion = "3.6.0"
  val specs2Version = "3.7-scalaz-7.1.6"

libraryDependencies ++=  Seq(
    "org.scalaz" %% "scalaz-core" % scalazVersion,
    "org.scalaz" %% "scalaz-iteratee" % scalazVersion,
    "org.scalaz" %% "scalaz-effect" % scalazVersion,
    "org.scalaz" %% "scalaz-concurrent" % scalazVersion % "test",
    "net.liftweb" %% "lift-json-scalaz7" % liftJsonVersion,
		"com.typesafe.play" %% "play" % "2.4.6",
		"com.typesafe.play" %% "play-cache" % "2.4.6",
		"jp.t2v" %% "play2-auth"        % "0.14.1",
		"io.jvm.uuid" %% "scala-uuid" % "0.2.1",
    "io.megam" %% "scaliak" % "0.16",
    "com.rabbitmq" % "amqp-client" % amqpVersion,
    "org.specs2" %% "specs2-core" % specs2Version % "test" excludeAll (
      ExclusionRule("org.specs2", "org.specs2.io")),
    "org.apache.commons" % "commons-lang3" % "3.4",
		"org.walkmod" % "nsq-java" % "1.1",
		"org.eclipse.jgit" % "org.eclipse.jgit" % "4.2.0.201601211800-r")
}
