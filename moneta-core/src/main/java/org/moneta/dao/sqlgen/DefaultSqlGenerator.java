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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.moneta.dao.types.SqlStatement;
import org.moneta.types.search.CompositeCriteria;
import org.moneta.types.search.Criteria;
import org.moneta.types.search.FilterCriteria;
import org.moneta.types.topic.Topic;

/**
 * Default SQL generator for ANSI-standard Sql.
 * @author D. Ashmore
 *
 */
public class DefaultSqlGenerator implements SqlGenerator {
	
	protected static enum LockIntent {NONE, ROW};
	private Map<FilterCriteria.Operation, String> operationSymbolMap = new HashMap<FilterCriteria.Operation, String>();
	
	public DefaultSqlGenerator() {
		operationSymbolMap.put(FilterCriteria.Operation.EQUAL, "=");
		operationSymbolMap.put(FilterCriteria.Operation.GREATER_THAN, ">");
		operationSymbolMap.put(FilterCriteria.Operation.IS_NOT_NULL, " is not null ");
		operationSymbolMap.put(FilterCriteria.Operation.IS_NULL, " is null ");
		operationSymbolMap.put(FilterCriteria.Operation.LESS_THAN, "<");
		operationSymbolMap.put(FilterCriteria.Operation.LIKE, " like ");
		operationSymbolMap.put(FilterCriteria.Operation.NOT_EQUAL, "<>");
	}

	public SqlStatement generateSelect(Topic topic, CompositeCriteria searchCriteria,
			String[] fieldNames) {

		StringBuilder builder = new StringBuilder(this.generateSelectClause(fieldNames));
		builder.append(" ");
		builder.append(this.generateFromClause(topic, LockIntent.NONE));
		
		SqlStatement statement = null;
		if (searchCriteria != null) {
			builder.append(" ");
			statement = this.generateWhereClause(searchCriteria);
			builder.append(statement.getSqlText());
		}
		else {
			statement = new SqlStatement();
		}
		statement.setSqlText(builder.toString());
		return statement;
	}
	
	protected SqlStatement generateWhereClause(CompositeCriteria searchCriteria) {
		if (searchCriteria.getSearchCriteria() == null) {
			return new SqlStatement("");
		}
		if (searchCriteria.getSearchCriteria().length == 0) {
			return new SqlStatement("");
		}
		SqlStatement sqlStatement = new SqlStatement();
		StringBuilder builder = new StringBuilder("where ");
		
		for (Criteria criteria : searchCriteria.getSearchCriteria()) {
			if (builder.length() == 0) {
				builder.append("where ");
			}
			else {
				appendCriteria(builder, criteria, sqlStatement.getHostVariableValueList());
			}
		}
		sqlStatement.setSqlText(builder.toString());

		return sqlStatement;
	}
	
	protected String formatCompositeCriteria(CompositeCriteria compositeCriteria, List<Object> hostVariableValueList) {
		Validate.notNull(compositeCriteria.getOperator(), "Null operator not allowed.  criteria="+compositeCriteria);
		if (compositeCriteria.getSearchCriteria() == null) {
			return "";
		}
		if (compositeCriteria.getSearchCriteria().length == 0) {
			return "";
		}
		
		StringBuilder builder = new StringBuilder("(");
		for (Criteria criteria: compositeCriteria.getSearchCriteria()) {
			if (builder.length() > 1) {
				builder.append(" ");
				builder.append(compositeCriteria.getOperator().toString().toLowerCase());
				builder.append(" ");
			}
			
			appendCriteria(builder, criteria, hostVariableValueList);
		}
		builder.append(")");
		return builder.toString();
	}

	protected void appendCriteria(StringBuilder builder, Criteria criteria, List<Object> hostVariableValueList) {
		if (criteria instanceof FilterCriteria) {
			builder.append(this.formatFilterCriteria((FilterCriteria) criteria, hostVariableValueList));
		}
		else {
			builder.append(this.formatCompositeCriteria((CompositeCriteria) criteria, hostVariableValueList));
		}
	}
	
	protected String formatFilterCriteria(FilterCriteria filterCriteria, List<Object> hostVariableValueList) {
		Validate.notNull(filterCriteria.getOperation(), "Null operation not allowed.  criteria="+filterCriteria);
		Validate.notEmpty(filterCriteria.getFieldName(), "Null or blank field name not allowed.");
		
		StringBuilder builder = new StringBuilder();
		builder.append(filterCriteria.getFieldName());
		builder.append(" ");
		builder.append(this.formatConditionOperation(filterCriteria.getOperation()));
		if ( !FilterCriteria.Operation.IS_NULL.equals(filterCriteria.getOperation()) && 
				!FilterCriteria.Operation.IS_NOT_NULL.equals(filterCriteria.getOperation())) {
			hostVariableValueList.add(filterCriteria.getValue());
		}

		return builder.toString();
	}
	
	protected String formatConditionOperation(FilterCriteria.Operation operation) {
		return operationSymbolMap.get(operation);
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
