megam_common
==========

libmegam contains the common libraries in scala with a funcational twist.

* `AMQP` : AMQP client
* `NSQ` :  NSQ  scaffolding mediator to the (nsq-java walkmod)


### Requirements

>
[NSQ.io 3.5.x +](http://www.rabbitmq.com)
[OpenJDK 8.0](http://openjdk.java.net/install/index.html)



#### Tested on Ubuntu 14.04

## Building

```shell

sbt

clean

compile

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

For a sample, Refer [megam gateway](https://github.com/megamsys/vertice_gateway.git)


### sbt

Before your run it,


```scala

	resolvers ++= Seq(Resolver.sonatypeRepo("releases"),
	Resolver.sonatypeRepo("snapshots"),
	Resolver.bintrayRepo("scalaz", "releases"),
	Resolver.bintrayRepo("io.megam", "scala"))

	libraryDependencies += "io.megam" % "libcommon" % "1.5.rc5"

```


We are glad to help if you have questions, or request for new features..

[twitter](http://twitter.com/megamsys) [email](<support@megam.io>)


# License

MIT
