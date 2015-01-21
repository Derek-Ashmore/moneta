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
package org.moneta.dao.types;

import java.util.ArrayList;
import java.util.List;

import org.moneta.types.BaseType;

public class SqlStatement extends BaseType {
	
	public SqlStatement()  {}
	public SqlStatement(String sqlText)  {
		this.setSqlText(sqlText);
	}
	
	private String sqlText;
	private List<Object> hostVariableValueList = new ArrayList<Object>();
	
	public String getSqlText() {
		return sqlText;
	}
	
	public void setSqlText(String sqlText) {
		this.sqlText = sqlText;
	}

	public List<Object> getHostVariableValueList() {
		return hostVariableValueList;
	}
	

}
