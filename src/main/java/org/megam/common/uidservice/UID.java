/* 
 ** Copyright [2012-2013] [Megam Systems]
 **
 ** Licensed under the Apache License, Version 2.0 (the "License");
 ** you may not use this file except in compliance with the License.
 ** You may obtain a copy of the License at
 **
 ** http://www.apache.org/licenses/LICENSE-2.0
 **
 ** Unless required by applicable law or agreed to in writing, software
 ** distributed under the License is distributed on an "AS IS" BASIS,
 ** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ** See the License for the specific language governing permissions and
 ** limitations under the License.
 */
package org.megam.common.uidservice;

/**
 * @author ram
 *
 */

import org.apache.thrift.*;
import org.apache.thrift.async.*;
import org.apache.thrift.meta_data.*;
import org.apache.thrift.transport.*;
import org.apache.thrift.protocol.*;

import com.twitter.service.snowflake.gen.*;


public class UID {

	public UID(String count, String servers, String agent) throws Exception {

		String host = "localhost";
		int port = 7609;

		TTransport transport = new TSocket(host, port);
		transport.open();

		TProtocol protocol = new TBinaryProtocol(transport);

		Snowflake.Client client = new Snowflake.Client(protocol);

		long worker_id = client.get_id(agent);

		System.out.println("worker_id" + worker_id);

	}

}
