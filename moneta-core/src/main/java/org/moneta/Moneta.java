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
package org.moneta;

import java.util.ArrayList;
import java.util.List;

import org.moneta.config.MonetaEnvironment;
import org.moneta.dao.MonetaSearchDAO;
import org.moneta.types.Record;
import org.moneta.types.Value;
import org.moneta.types.search.SearchRequest;
import org.moneta.types.search.SearchResult;
import org.moneta.types.topic.Topic;

/**
 * Base Moneta class providing search and store capability.
 * @author D. Ashmore
 *
 */
public class Moneta {
	
	public SearchResult find(SearchRequest request) {
		return new MonetaSearchDAO().find(request);
	}
	
	public SearchResult findAllTopics() {
		SearchResult result = new SearchResult();
		List<Record> recordList = new ArrayList<Record>();
		Record record = null;
		List<Value> valueList = null;
		
		for (Topic topic: MonetaEnvironment.getConfiguration().getTopicList()) {
			record = new Record();
			recordList.add(record);
			
			valueList =  new ArrayList<Value>();
			valueList.add(new Value("Topic", topic.getTopicName()));
			valueList.add(new Value("Catalog", topic.getCatalogName()));
			valueList.add(new Value("Schema", topic.getSchemaName()));
			valueList.add(new Value("TableName", topic.getTableName()));
			valueList.add(new Value("DataSource", topic.getDataSourceName()));
			record.setValues(valueList.toArray(new Value[0]));
		}
		
		result.setResultData(recordList.toArray(new Record[0]));
		return result;
	}
	

}
