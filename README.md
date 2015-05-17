megam_common
==========

libmegam contains the common libraries in scala with a funcational twist.

* `AMQP` : AMQP client
* `Riak` : Riak scaffolding mediator to the [scaliak driver by stackmob](https://github.com/stackmob/scaliak)
* `UID`  : A Unique identity generating service client for scala which connects to python based twitters [snowflake](https://github.com/twitter/snowflake)


### Requirements

>
[RabbitMQ 3.5.x +](http://www.rabbitmq.com)
[OpenJDK 8.0](http://openjdk.java.net/install/index.html)
[Riak 2.1.1 +](http://basho.com)
[Python Snowflake server](http://github.com/megamsys/pysnowflake)


#### Tested on Ubuntu 14.04

## Building

* You'll need `thrift 0.9.2 compiler`

```shell

sbt

clean

thrift:generate-java

compile

```


## Usage

### Play Framework

For a sample, Refer [megam gateway](https://github.com/megamsys/megam_gateway.git)


### sbt

Before your run it,


```scala

	resolvers ++= Seq(Resolver.sonatypeRepo("releases"),
	Resolver.sonatypeRepo("snapshots"),
	Resolver.bintrayRepo("scalaz", "releases"),
	Resolver.bintrayRepo("io.megam", "scala"))

	libraryDependencies += "io.megam" % "libmegam" % "0.8"

```


We are glad to help if you have questions, or request for new features..

[twitter](http://twitter.com/indykish) [email](<rajthilak@megam.co.in>)


# License


|                      |                                          |
|:---------------------|:-----------------------------------------|
| **Author:**          | Rajthilak (<rajthilak@megam.co.in>)
|		               | KishorekumarNeelamegam (<nkishore@megam.co.in>)
| **Copyright:**       | Copyright (c) 2012-2013 Megam Systems.
| **License:**         | Apache License, Version 2.0

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
