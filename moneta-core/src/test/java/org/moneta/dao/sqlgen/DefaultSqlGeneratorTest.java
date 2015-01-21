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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
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
	}
	
	@Test
	public void testFormatFilterCriteria() throws Exception {
		
	}

}
