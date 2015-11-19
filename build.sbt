import sbt._
import sbt.Keys._
import com.github.bigtoast.sbtthrift.ThriftPlugin

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

incOptions := incOptions.value.withNameHashing(true)

resolvers ++= Seq(Resolver.sonatypeRepo("releases"),
Resolver.sonatypeRepo("snapshots"),
Resolver.bintrayRepo("scalaz", "releases"),
Resolver.bintrayRepo("io.megam", "scala")
)

{
  val scalazVersion = "7.1.5"
  val liftJsonVersion = "3.0-M6"
  val amqpVersion = "3.5.6"
  val specs2Version = "3.6.5-20151108070227-1e34889"

libraryDependencies ++=  Seq(
    "org.scalaz" %% "scalaz-core" % scalazVersion,
    "org.scalaz" %% "scalaz-iteratee" % scalazVersion,
    "org.scalaz" %% "scalaz-effect" % scalazVersion,
    "org.scalaz" %% "scalaz-concurrent" % scalazVersion % "test",
    "net.liftweb" %% "lift-json-scalaz7" % liftJsonVersion,
    "io.megam" %% "scaliak" % "0.15",
    "com.rabbitmq" % "amqp-client" % amqpVersion,
    "org.specs2" %% "specs2-core" % specs2Version % "test",
    "org.apache.commons" % "commons-lang3" % "3.4",
    "org.apache.thrift" % "libthrift" % "0.9.3" excludeAll (
      ExclusionRule("commons-logging", "commons-logging"),
      ExclusionRule("org.slf4j","slf4j-simple"),
      ExclusionRule("org.slf4j","slf4j-nop"),
      ExclusionRule("org.slf4j", "slf4j-jdk14"))
    )
}

seq(ThriftPlugin.thriftSettings: _*)
