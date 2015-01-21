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

import org.apache.commons.lang3.Validate;
import org.moneta.config.MonetaEnvironment;
import org.moneta.dao.sqlgen.SqlGeneratorFactory;
import org.moneta.dao.types.SqlStatement;
import org.moneta.types.search.SearchRequest;
import org.moneta.types.search.SearchResult;
import org.moneta.types.topic.MonetaDataSource;
import org.moneta.types.topic.Topic;

public class MonetaSearchDAO extends BaseDAO {

	public SearchResult find(SearchRequest request) {
		Validate.notNull(request, "Null search request not allowed.");
		Validate.notEmpty(request.getTopic(), "Null or blank search topic not allowed.");
		
		Topic searchTopic = MonetaEnvironment.getConfiguration().getTopic(request.getTopic());
		Validate.notNull(searchTopic, "Search topic not found.   topic="+request.getTopic());
		
		MonetaDataSource source = MonetaEnvironment.getConfiguration().getMonetaDataSource(searchTopic.getDataSourceName());
		
		SqlStatement sqlStmt = SqlGeneratorFactory.findSqlGenerator(source.getDialect())
				.generateSelect(searchTopic, 
						request.getSearchCriteria(), 
						request.getFieldNames());
		SqlSelectExecutor sExec = new SqlSelectExecutor(request.getTopic(), sqlStmt);
		sExec.setMaxRows(request.getMaxRows());
		sExec.setStartRow(request.getStartRow());
		return sExec.call();
	}
}
