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

import org.apache.commons.dbutils.ResultSetHandler;
import org.moneta.types.Record;
import org.moneta.types.Value;

class RecordResultSetHandler implements ResultSetHandler<Record[]> {

	public Record[] handle(ResultSet rSet) throws SQLException {
		ResultSetMetaData meta = rSet.getMetaData();
		List<Record> recordList = new ArrayList<Record>();
		Record record = null;
		List<Value> valueList = null;
		
		while (rSet.next()) {
			record = new Record();
			recordList.add(record);
						
			valueList = new ArrayList<Value>();
			for (int columnIndex = 1; columnIndex <= meta.getColumnCount(); columnIndex++) {
				// TODO Normalize values (e.g. long varchars, etc.
				valueList.add(new Value(meta.getColumnName(columnIndex), rSet.getObject(columnIndex)));
			}
			record.setValues(valueList.toArray(new Value[0]));
		}

		return recordList.toArray(new Record[0]);
	}

}
