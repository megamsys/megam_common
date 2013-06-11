megam_common
==========

Megam common libraries for scala, Java. 

* `AMQP` : AMQP client
* `Zoo`  : Zookeeper client
* `Riak` : Riak scaffolding mediator to the [scaliak driver by stackmob](https://github.com/stackmob/scaliak)
* `UID`  : A Unique identity generating service client for scala which connects to twitters [snowflake](https://github.com/twitter/snowflake)


### Requirements

> 
[RabbitMQ 3.0.4 +](http://www.rabbitmq.com)
[OpenJDK 7.0](http://openjdk.java.net/install/index.html)
[Riak 1.3.1 +](http://basho.com)
[Snowflake server](http://github.com/twitter/snowflake) 

* Snowflake Ubuntu DEB Bundle(13.04) is available here 



#### Tested on Ubuntu 13.04, AWS - EC2

## Usage

We'll see the usage in `akka`, `play` or `standalone programs in Scala`

* Akka
* Play framework
* Any other standalone (`extends App` in Scala)

At the minimum you need a configuration file with the following. 

### Akka

// The global settings file loaded as an extension.

```json



app { 
	
	amqp { 
		uris = ["amqp://megam:team4megam@rabbitmq1.megam.co.in:5200/megam","amqp://megam:team4megam@rabbitmq2.megam.co.in:5200/megam"], 
		exchange = "megam_exchange",		
		queue = "megam_conf"
	} 
	zoo {
	
	}
}

```

### Play Framework 

// The global settings file loaded as an extension.

```json



app { 
	
	amqp { 
		uris = ["amqp://megam:team4megam@rabbitmq1.megam.co.in:5200/megam","amqp://megam:team4megam@rabbitmq2.megam.co.in:5200/megam"], 
		exchange = "megam_exchange",		
		queue = "megam_conf"
	} 
	zoo {
	
	}
}

```

###



### Prepare your program

Before your run it,

* RabbitMQ Server is running

> Add this maven dependency

```xml
	<dependency>
	<groupId>com.github.indykish</groupId>
	<artifactId>megam_common</artifactId>
	<version>0.1.0-SNAPSHOT</version>
	</dependency>
```
### Akka

* Invoking AMQP Client as an extension in akka

```scala


```

### Play

* Invoking AMQP Client as an extension in akka

```scala


```
   


We are glad to help if you have questions, or request for new features..

[twitter](http://twitter.com/indykish) [email](<rajthilak@megam.co.in>)

#### TO - DO

* Logging/Better Failure handling
* Testing in progress

	
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
 
