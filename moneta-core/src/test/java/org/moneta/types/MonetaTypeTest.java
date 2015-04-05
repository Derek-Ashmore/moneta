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
package org.moneta.types;



import org.force66.beantester.BeanTester;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.moneta.types.search.CompositeCriteria;
import org.moneta.types.search.FilterCriteria;
import org.moneta.types.search.SearchRequest;
import org.moneta.types.search.SearchResult;
import org.moneta.types.topic.Topic;
import org.moneta.types.topic.TopicKeyField;

public class MonetaTypeTest {
	
	private BeanTester beanTester;

	@Before
	public void setUp() throws Exception {
		beanTester = new BeanTester();
	}

	@Test
	public void test() throws Exception {
		beanTester.testBean(CompositeCriteria.class);
		beanTester.testBean(Record.class);
		beanTester.testBean(FilterCriteria.class);
		beanTester.testBean(SearchRequest.class);
		beanTester.testBean(SearchResult.class);
		beanTester.testBean(Value.class);
		
		// topic package tests
		beanTester.testBean(Topic.class);
		beanTester.testBean(TopicKeyField.class);
	}
	
	@Test
	public void testTopicCompare() throws Exception {
		Topic t1 = new Topic();
		Topic t2 = new Topic();
		
		Assert.assertTrue(t1.compareTo(t2) == 0);
		t1.setTopicName("a");
		Assert.assertTrue(t1.compareTo(t2) > 0);
		t2.setTopicName("b");
		Assert.assertTrue(t1.compareTo(t2) < 0);
	}

}
