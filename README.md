megam_common
==========

libcommon contains the common scala libraries with a funcational twist used by Megam.

* `AMQP` : AMQP abstract client
* `NSQ`  : NSQ  scaffolding mediator to the (nsq-java walkmod)
* `UID`  : Unique id generator.
* `Auth` : Salted auth using Pkbd12

### Requirements

[NSQ.io 0.3.x +](http://www.nsq.io)
[OpenJDK 8.0](http://openjdk.java.net/install/index.html)



## Building

```

sbt

#from the sbt REPL

> clean

> compile

```

#Publishing in bintray

For more information [https://github.com/softprops/bintray-sbt](https://github.com/softprops/bintray-sbt)

```
#from the sbt REPL

> bintrayChangeCredentials

> bintrayWhoami

> publish

```

## Usage

### Play Framework

Refer [megam gateway](https://github.com/megamsys/vertice_gateway.git)


### sbt

Before your run it,


```scala

	resolvers ++= Seq(Resolver.sonatypeRepo("releases"),
	Resolver.sonatypeRepo("snapshots"),
	Resolver.bintrayRepo("scalaz", "releases"),
	Resolver.bintrayRepo("io.megam", "scala"))

	libraryDependencies += "io.megam" % "libcommon" % "1.5.1"

```


We are glad to help if you have questions, or request for new features..

[twitter](http://twitter.com/megamsys) [email](<support@megam.io>)


# License

APACHE-V2
