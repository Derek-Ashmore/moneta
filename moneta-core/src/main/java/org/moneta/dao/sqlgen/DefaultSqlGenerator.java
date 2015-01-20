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
package org.moneta.dao.sqlgen;

import org.apache.commons.lang3.StringUtils;
import org.moneta.types.search.SearchCriteria;
import org.moneta.types.topic.Topic;

public class DefaultSqlGenerator implements SqlGenerator {
	
	protected static enum LockIntent {NONE, ROW};

	public String generateSelect(Topic topic, SearchCriteria[] searchCriteria,
			String[] fieldNames) {
		// TODO Add search criteria logic
		StringBuilder builder = new StringBuilder(this.generateSelectClause(fieldNames));
		builder.append(" ");
		builder.append(this.generateFromClause(topic, LockIntent.NONE));
		return builder.toString();
	}
	
	protected String generateSelectClause(String[] fieldNames) {
		StringBuilder builder = new StringBuilder("select ");
		
		if (fieldNames == null) {
			builder.append("*");
		}
		else {
			builder.append(StringUtils.join(fieldNames, ','));
		}
		
		return builder.toString();
	}
	
	protected String generateFromClause(Topic topic, LockIntent lockIntent) {
		StringBuilder builder = new StringBuilder("from ");
		
		if ( !StringUtils.isEmpty(topic.getSchemaName())) {
			builder.append(this.formatSchemaName(topic.getSchemaName()));
			builder.append(".");
		}
		builder.append(this.formatTableName(topic.getTableName(), lockIntent));

		return builder.toString();
	}
	
	protected String formatSchemaName(String schemaName) {
		return schemaName;
	}
	
	protected String formatTableName(String tableName, LockIntent lockIntent) {
		return tableName;
	}

}
