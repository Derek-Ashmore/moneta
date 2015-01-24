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

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.apache.commons.dbutils.ResultSetHandler;
import org.moneta.types.Record;
import org.moneta.types.Value;

class RecordResultSetHandler implements ResultSetHandler<Record[]> {
	
	private Long maxRows=null;
	private Long startRow=null;
	private Map<String,String> aliasMap = new CaseInsensitiveMap<String,String>();
	
	public RecordResultSetHandler() {}

	public Record[] handle(ResultSet rSet) throws SQLException {
		ResultSetMetaData meta = rSet.getMetaData();
		List<Record> recordList = new ArrayList<Record>();
		Record record = null;
		List<Value> valueList = null;
		
		if (startRow != null && startRow > 0) {
			for (int i = 0; i < startRow - 1 && rSet.next(); i++);
		}
		
		long nbrRows = 0;
		String columnName;
		while (rSet.next()) {
			record = new Record();
			recordList.add(record);
						
			valueList = new ArrayList<Value>();
			for (int columnIndex = 1; columnIndex <= meta.getColumnCount(); columnIndex++) {
				// TODO Normalize values (e.g. long varchars, etc.
				
				// Alias field impl
				columnName=meta.getColumnName(columnIndex);
				if (this.getAliasMap().containsKey(columnName)) {
					columnName=this.getAliasMap().get(columnName);
				}
				
				valueList.add(new Value(columnName, rSet.getObject(columnIndex)));
			}
			record.setValues(valueList.toArray(new Value[0]));
			
			nbrRows++;
			if (maxRows != null && maxRows > 0 && nbrRows >= maxRows) {
				break;
			}
		}

		return recordList.toArray(new Record[0]);
	}

	public Long getMaxRows() {
		return maxRows;
	}

	public void setMaxRows(Long maxRows) {
		this.maxRows = maxRows;
	}

	public Long getStartRow() {
		return startRow;
	}

	public void setStartRow(Long startRow) {
		this.startRow = startRow;
	}

	public Map<String, String> getAliasMap() {
		return aliasMap;
	}

}
