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
package org.moneta.types.topic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.moneta.types.BaseType;

/**
 * Represents an information topic.
 * @author D. Ashmore
 *
 */
public class Topic extends BaseType implements Comparable<Topic> {
	
	private String topicName;
	private String dataSourceName;
	private String schemaName;
	private String catalogName;
	private String tableName;
	private Boolean readOnly = Boolean.FALSE;
	
	private Map<String,String> aliasMap = new HashMap<String,String>();
	private List<TopicKeyField> keyFieldList = new ArrayList<TopicKeyField>();
	
	public String getTopicName() {
		return topicName;
	}
	
	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}
	
	public String getTableName() {
		return tableName;
	}
	
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getSchemaName() {
		return schemaName;
	}

	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

	public Boolean getReadOnly() {
		return readOnly;
	}

	public void setReadOnly(Boolean readOnly) {
		this.readOnly = readOnly;
	}

	public String getDataSourceName() {
		return dataSourceName;
	}

	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}

	public String getCatalogName() {
		return catalogName;
	}

	public void setCatalogName(String catalogName) {
		this.catalogName = catalogName;
	}

	public Map<String, String> getAliasMap() {
		return aliasMap;
	}

	public List<TopicKeyField> getKeyFieldList() {
		return keyFieldList;
	}

	public int compareTo(Topic other) {
		return new CompareToBuilder()
			.append(this.topicName, other.getTopicName())
			.toComparison();
	}

}
