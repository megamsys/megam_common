import sbt._
import sbt.Keys._

name := "libcommon"

organization := "io.megam"

scalaVersion := "2.11.6"

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
  	"-Ywarn-all",
  	"-Yclosure-elim",
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
  val scalazVersion = "7.1.2"
  val liftJsonVersion = "3.0-M5-1"
  val amqpVersion = "3.3.4"
  val specs2Version = "3.6"

  Seq(
    "org.scalaz" %% "scalaz-core" % scalazVersion,
    "net.liftweb" %% "lift-json-scalaz7" % liftJsonVersion,
    "io.megam" %% "scaliak" % "0.9.0",
    "com.rabbitmq" % "amqp-client" % amqpVersion,
    "org.specs2" %% "specs2" % specs2Version % "test",
    "org.apache.commons" % "commons-lang3" % "3.3.2")
}

logBuffered := false

lazy val commonSettings = Seq(
  version in ThisBuild := "0.8",
  organization in ThisBuild := "Megam Systems"
)

lazy val root = (project in file(".")).
  settings(commonSettings).
  settings(
    sbtPlugin := true,
    name := "libcommon",
    description := """This is a set of function libraries used in our servers. This contains amqp, json, riak and an unique id thrift client based on snowflake all built using a funcitonal twist.
    Feel free to collaborate at https://github.com/megamsys/megam_common.git.""",
    licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html")),
    publishMavenStyle := false,
    bintrayOrganization := Some("megamsys"),
    bintrayRepository := "scala"
  )
