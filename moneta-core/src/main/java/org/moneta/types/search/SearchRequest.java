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
package org.moneta.types.search;

import org.moneta.types.BaseType;

/**
 * Specifies a search request
 * @author D. Ashmore
 *
 */
public class SearchRequest extends BaseType {
	
	private String topic;
	private String[] fieldNames;
	private SearchCriteria[] searchCriteria;
	private Long maxRows;
	private Long startRow;
	
	public String[] getFieldNames() {
		return fieldNames;
	}
	
	public void setFieldNames(String[] fieldNames) {
		this.fieldNames = fieldNames;
	}
	
	public SearchCriteria[] getSearchCriteria() {
		return searchCriteria;
	}
	
	public void setSearchCriteria(SearchCriteria[] searchCriteria) {
		this.searchCriteria = searchCriteria;
	}

	public Long getMaxRows() {
		return maxRows;
	}

	public void setMaxRows(Long maxRows) {
		this.maxRows = maxRows;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public Long getStartRow() {
		return startRow;
	}

	public void setStartRow(Long startRow) {
		this.startRow = startRow;
	}


}
