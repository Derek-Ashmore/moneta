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

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.moneta.dao.types.SqlStatement;
import org.moneta.types.search.CompositeCriteria;
import org.moneta.types.search.Criteria;
import org.moneta.types.search.FilterCriteria;
import org.moneta.types.topic.Topic;

public class DefaultSqlGeneratorTest {
	
	DefaultSqlGenerator generator;
	Topic topic;
	String[] fieldNames = new String[]{"col1","col2","col3"};

	@Before
	public void setUp() throws Exception {
		generator = new DefaultSqlGenerator();
		topic = new Topic();
		topic.setTableName("myTable");
		topic.setSchemaName("mySchema");
	}

	@Test
	public void testFormatTableName() throws Exception {
		Assert.assertTrue("foo".equals(generator.formatTableName("foo", DefaultSqlGenerator.LockIntent.NONE)));;
	}
	
	@Test
	public void testFormatSchemaName() throws Exception {
		Assert.assertTrue("foo".equals(generator.formatSchemaName("foo")));;
	}
	
	@Test
	public void testGenerateFromClause() throws Exception {
		Assert.assertTrue("from mySchema.myTable".equals(
				generator.generateFromClause(topic, DefaultSqlGenerator.LockIntent.NONE)));
		topic.setSchemaName(null);
		Assert.assertTrue("from myTable".equals(
				generator.generateFromClause(topic, DefaultSqlGenerator.LockIntent.NONE)));
	}
	
	@Test
	public void testGenerateSelectClause() throws Exception {
		Assert.assertTrue("select *".equals(
				generator.generateSelectClause(null)));
		Assert.assertTrue("select col1,col2,col3".equals(
				generator.generateSelectClause(fieldNames)));
	}
	
	@Test
	public void testGenerateSelect() throws Exception {
		Assert.assertTrue("select col1,col2,col3 from mySchema.myTable".equals(
				generator.generateSelect(topic, null, fieldNames).getSqlText()));
		
		FilterCriteria fCrit1 = createFilterCriteria("myCol1", FilterCriteria.Operation.EQUAL, "fu");
		FilterCriteria fCrit2 = createFilterCriteria("myCol2", FilterCriteria.Operation.EQUAL, "bar");		
		CompositeCriteria cCrit2 = createCompositCriteria(new Criteria[]{fCrit1,fCrit2}, CompositeCriteria.Operator.OR);
		CompositeCriteria compositeCriteria = createCompositCriteria(new Criteria[]{fCrit1,fCrit2,cCrit2}, CompositeCriteria.Operator.AND);
		
		System.out.println(generator.generateSelect(topic, compositeCriteria, fieldNames));
		SqlStatement stmt = generator.generateSelect(topic, compositeCriteria, fieldNames);
		Assert.assertTrue("select col1,col2,col3 from mySchema.myTable where  and myCol1 =? and myCol2 =? and (myCol1 =? or myCol2 =?)".equals(stmt.getSqlText()));
		Assert.assertTrue(stmt.getHostVariableValueList().size()==4);
		Assert.assertTrue("fu".equals(stmt.getHostVariableValueList().get(0)));
		Assert.assertTrue("bar".equals(stmt.getHostVariableValueList().get(1)));
		Assert.assertTrue("fu".equals(stmt.getHostVariableValueList().get(2)));
		Assert.assertTrue("bar".equals(stmt.getHostVariableValueList().get(3)));
	}
	
	@Test
	public void testFormatFilterCriteria() throws Exception {
		
		FilterCriteria filterCriteria = new FilterCriteria();
		List<Object> hostVariableValueList = new ArrayList<Object>();
		
		testformatFilterCriteriaException(null,
				hostVariableValueList, "filterCriteria");
		testformatFilterCriteriaException(filterCriteria,
				hostVariableValueList, "operation");
		
		filterCriteria.setOperation(FilterCriteria.Operation.EQUAL);
		testformatFilterCriteriaException(filterCriteria,
				hostVariableValueList, "field name");
		
		filterCriteria.setFieldName("myField");
		filterCriteria.setValue("foo");
		Assert.assertTrue("myField =?".equals(generator.formatFilterCriteria(filterCriteria, hostVariableValueList)));
		Assert.assertTrue(hostVariableValueList.size()==1);
		Assert.assertTrue("foo".equals(hostVariableValueList.get(0)));
		
		hostVariableValueList.clear();
		filterCriteria.setOperation(FilterCriteria.Operation.IS_NULL);
		Assert.assertTrue("myField  is null ".equals(generator.formatFilterCriteria(filterCriteria, hostVariableValueList)));
		Assert.assertTrue(hostVariableValueList.size()==0);
		
		hostVariableValueList.clear();
		filterCriteria.setOperation(FilterCriteria.Operation.IS_NOT_NULL);
		Assert.assertTrue("myField  is not null ".equals(generator.formatFilterCriteria(filterCriteria, hostVariableValueList)));
		Assert.assertTrue(hostVariableValueList.size()==0);

	}
	
	@Test
	public void testGenerateWhereClause() throws Exception {
		CompositeCriteria compositeCriteria = new CompositeCriteria();
		compositeCriteria.setOperator(CompositeCriteria.Operator.AND);
		
		FilterCriteria fCrit1 = createFilterCriteria("myCol1", FilterCriteria.Operation.EQUAL, "fu");
		FilterCriteria fCrit2 = createFilterCriteria("myCol2", FilterCriteria.Operation.EQUAL, "bar");		
		CompositeCriteria cCrit2 = createCompositCriteria(new Criteria[]{fCrit1,fCrit2}, CompositeCriteria.Operator.OR);
		
		SqlStatement stmt = generator.generateWhereClause(compositeCriteria);
		Assert.assertTrue("".equals(stmt.getSqlText()));
		Assert.assertTrue(stmt.getHostVariableValueList().size()==0);
		compositeCriteria.setSearchCriteria(new CompositeCriteria[0]);
		stmt = generator.generateWhereClause(compositeCriteria);
		Assert.assertTrue("".equals(stmt.getSqlText()));
		Assert.assertTrue(stmt.getHostVariableValueList().size()==0);
		
		compositeCriteria.setSearchCriteria(new Criteria[]{fCrit1,fCrit2,cCrit2});
		System.out.println(generator.generateWhereClause(compositeCriteria));
		stmt = generator.generateWhereClause(compositeCriteria);
		Assert.assertTrue("where  and myCol1 =? and myCol2 =? and (myCol1 =? or myCol2 =?)".equals(stmt.getSqlText()));
		Assert.assertTrue(stmt.getHostVariableValueList().size()==4);
		Assert.assertTrue("fu".equals(stmt.getHostVariableValueList().get(0)));
		Assert.assertTrue("bar".equals(stmt.getHostVariableValueList().get(1)));
		Assert.assertTrue("fu".equals(stmt.getHostVariableValueList().get(2)));
		Assert.assertTrue("bar".equals(stmt.getHostVariableValueList().get(3)));
	}
	
	/**
	 * Tests appendCriteria as well.
	 * @throws Exception
	 */
	@Test
	public void testFormatCompositeCriteria() throws Exception {
		CompositeCriteria compositeCriteria = new CompositeCriteria();
		List<Object> hostVariableValueList = new ArrayList<Object>();
		
		Throwable exceptionThrown = null;
		try {generator.formatCompositeCriteria(null, hostVariableValueList);} 
		catch (Exception e) {
			exceptionThrown = e;
		}
		Assert.assertTrue(exceptionThrown != null);
		Assert.assertTrue(exceptionThrown.getMessage().contains("compositeCriteria"));
		Assert.assertTrue(hostVariableValueList.size()==0);
		
		exceptionThrown = null;
		try {generator.formatCompositeCriteria(compositeCriteria, hostVariableValueList);} 
		catch (Exception e) {
			exceptionThrown = e;
		}
		Assert.assertTrue(exceptionThrown != null);
		Assert.assertTrue(exceptionThrown.getMessage().contains("operator"));
		Assert.assertTrue(hostVariableValueList.size()==0);
		
		compositeCriteria.setOperator(CompositeCriteria.Operator.AND);
		
		Assert.assertTrue("".equals(generator.formatCompositeCriteria(compositeCriteria, hostVariableValueList)));
		Assert.assertTrue(hostVariableValueList.size()==0);
		compositeCriteria.setSearchCriteria(new CompositeCriteria[0]);
		Assert.assertTrue("".equals(generator.formatCompositeCriteria(compositeCriteria, hostVariableValueList)));
		Assert.assertTrue(hostVariableValueList.size()==0);
		
		FilterCriteria fCrit1 = createFilterCriteria("myCol1", FilterCriteria.Operation.EQUAL, "fu");
		FilterCriteria fCrit2 = createFilterCriteria("myCol2", FilterCriteria.Operation.EQUAL, "bar");
		
		CompositeCriteria cCrit2 = createCompositCriteria(new Criteria[]{fCrit1,fCrit2}, CompositeCriteria.Operator.OR);
		
		compositeCriteria.setSearchCriteria(new Criteria[]{fCrit1,fCrit2,cCrit2});
		Assert.assertTrue("(myCol1 =? and myCol2 =? and (myCol1 =? or myCol2 =?))".equals(generator.formatCompositeCriteria(compositeCriteria, hostVariableValueList)));
		Assert.assertTrue(hostVariableValueList.size()==4);
		Assert.assertTrue("fu".equals(hostVariableValueList.get(0)));
		Assert.assertTrue("bar".equals(hostVariableValueList.get(1)));
		Assert.assertTrue("fu".equals(hostVariableValueList.get(2)));
		Assert.assertTrue("bar".equals(hostVariableValueList.get(3)));
	}

	private CompositeCriteria createCompositCriteria(Criteria[] fCrit,
			CompositeCriteria.Operator operator) {
		CompositeCriteria cCrit2 = new CompositeCriteria();
		cCrit2.setOperator(operator);
		cCrit2.setSearchCriteria(fCrit);
		return cCrit2;
	}

	private FilterCriteria createFilterCriteria(String fieldName, FilterCriteria.Operation operation, Object value) {
		FilterCriteria fCrit1 = new FilterCriteria();
		fCrit1.setFieldName(fieldName);
		fCrit1.setOperation(operation);
		fCrit1.setValue(value);
		return fCrit1;
	}

	protected void testformatFilterCriteriaException(
			FilterCriteria filterCriteria, List<Object> hostVariableValueList,
			String testPhrase) {
		Throwable exceptionThrown = null;
		try {generator.formatFilterCriteria(filterCriteria, hostVariableValueList);} 
		catch (Exception e) {
			exceptionThrown = e;
		}
		Assert.assertTrue(exceptionThrown != null);
		Assert.assertTrue(exceptionThrown.getMessage().contains(testPhrase));
	}
	
	@Test
	public void testFormatConditionOperation() throws Exception {
		Assert.assertTrue("=".equals(
				generator.formatConditionOperation(FilterCriteria.Operation.EQUAL)));
		Assert.assertTrue(">".equals(
				generator.formatConditionOperation(FilterCriteria.Operation.GREATER_THAN)));
		Assert.assertTrue(" is not null ".equals(
				generator.formatConditionOperation(FilterCriteria.Operation.IS_NOT_NULL)));
		Assert.assertTrue(" is null ".equals(
				generator.formatConditionOperation(FilterCriteria.Operation.IS_NULL)));
		Assert.assertTrue("<".equals(
				generator.formatConditionOperation(FilterCriteria.Operation.LESS_THAN)));
		Assert.assertTrue(" like ".equals(
				generator.formatConditionOperation(FilterCriteria.Operation.LIKE)));
		Assert.assertTrue("<>".equals(
				generator.formatConditionOperation(FilterCriteria.Operation.NOT_EQUAL)));
	}

}
