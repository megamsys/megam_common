import sbt._
import sbt.Keys._

name := "libcommon"

organization := "io.megam"

description := """This is a set of function libraries used in our servers. This contains amqp, json, riak and an unique id thrift client based on snowflake all built using a funcitonal twist.
Feel free to collaborate at https://github.com/megamsys/megam_common.git."""

licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html"))

scalaVersion := "2.11.6"

bintrayOrganization := Some("megamsys")

bintrayRepository := "scala"

crossScalaVersions := Seq("2.10.4", "2.11.2")

scalacOptions := Seq(
	"-target:jvm-1.8",
	"-deprecation",
	"-feature",
 	"-optimise",
  	"-Xcheckinit",
  	"-Xlint",
  	"-Xverify",
  	"-Yconst-opt",
  	"-Yinline",
  	"-Yclosure-elim",
  	"-language:postfixOps",
  	"-language:implicitConversions",
  	"-Ydead-code")

incOptions := incOptions.value.withNameHashing(true)

resolvers ++= Seq(Resolver.sonatypeRepo("releases"), Resolver.sonatypeRepo("snapshots"),Resolver.bintrayRepo("scalaz", "releases")
)

 	{
  val scalazVersion = "7.1.2"
  val liftJsonVersion = "3.0-M5-1"
  val amqpVersion = "3.5.1"
  val specs2Version = "3.6"

libraryDependencies ++=  Seq(
    "org.scalaz" %% "scalaz-core" % scalazVersion,
    "org.scalaz" %% "scalaz-iteratee" % scalazVersion,
    "org.scalaz" %% "scalaz-effect" % scalazVersion,
    "org.scalaz" %% "scalaz-concurrent" % scalazVersion % "test",
    "net.liftweb" %% "lift-json-scalaz7" % liftJsonVersion,
    "io.megam" %% "scaliak" % "0.13",
    "com.rabbitmq" % "amqp-client" % amqpVersion,
    "org.specs2" %% "specs2-core" % specs2Version % "test",
    "org.apache.commons" % "commons-lang3" % "3.3.2",
    "org.apache.thrift" % "libthrift" % "0.9.1" excludeAll (
      ExclusionRule("commons-logging", "commons-logging"),
      ExclusionRule("org.slf4j","slf4j-simple"),
      ExclusionRule("org.slf4j","slf4j-nop"),
      ExclusionRule("org.slf4j", "slf4j-jdk14"))
    )
}
