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

import org.moneta.types.Record;
import org.moneta.types.Value;
import org.moneta.types.search.SearchRequest;
import org.moneta.types.search.SearchResult;

/**
 * Base Moneta class providing search and store capability.
 * @author D. Ashmore
 *
 */
public class Moneta {
	
	public SearchResult find(SearchRequest request) {
		return this.createDummySearchResult();
	}
	
	private SearchResult createDummySearchResult() {
		SearchResult result = new SearchResult();
		
		Long nbrRecords = 2L;
		Integer nbrValues = 5;
		
		result.setErrorCode(0);
		result.setNbrRows(nbrRecords);
		
		Record[] record = new Record[nbrRecords.intValue()];
		result.setResultData(record);
		Value[] value = null;
		for (int i = 0; i < nbrRecords; i++) {
			record[i] = new Record();
			value = new Value[nbrValues];
			record[i].setValues(value);
			
			for (int j = 0; j < nbrValues; j++) {
				value[j] = new Value();
				value[j].setName("field" + j);
				value[j].setValue(Integer.valueOf(j));
			}
		}
		
		return result;
	}


}
