/*
 * This software is licensed under the Apache License, Version 2.0
 * (the "License") agreement; you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.moneta.dao;

import java.sql.Connection;

import org.apache.commons.lang3.Validate;
import org.moneta.config.MonetaEnvironment;
import org.moneta.types.topic.Topic;

abstract class BaseDAO {
	
	protected Connection getTopicConnection(String topicName) {
		Validate.notEmpty(topicName, "Null or blank search topic not allowed.");
		Topic topic = MonetaEnvironment.getConfiguration().getTopic(topicName);
		Validate.notNull(topic, "topic not found.    topic=" + topicName);
		
		return MonetaEnvironment.getConfiguration().getConnection(topic.getDataSourceName());
	}

}
