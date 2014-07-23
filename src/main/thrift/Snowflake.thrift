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
namespace java org.megam.service.snowflake.gen

exception InvalidSystemClock {
  1: string message,
}

exception InvalidUserAgentError {
  1: string message,
}

service Snowflake {
  i64 get_worker_id()
  i64 get_timestamp()
  i64 get_id(1:string useragent)
  i64 get_datacenter_id()
}

struct AuditLogEntry {
  1: i64 id,
  2: string useragent,
  3: i64 tag
}