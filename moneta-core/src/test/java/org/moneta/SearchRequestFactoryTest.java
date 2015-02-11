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

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.moneta.config.MonetaEnvironment;
import org.moneta.types.search.CompositeCriteria;
import org.moneta.types.search.Criteria;
import org.moneta.types.search.FilterCriteria;
import org.moneta.types.search.SearchRequest;

public class SearchRequestFactoryTest extends MonetaTestBase {
	
	private SearchRequestFactory factory;
	private MockRequest request;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		factory = new SearchRequestFactory();
		request = new MockRequest();
	}

	@Test
	public void testDeriveSearchRequest() throws Exception {
		request.setUri("/myapp", null);
		testException("Search topic not provided");
		
		request.setUri("/myapp", "/crap");
		testException("Topic not configured");
		testException("crap");
		
		request.setUri("/myapp", "/Environment/one/too/too/many");
		testException("Search key in request uri not configured");
		testException("many");
		
		request.setUri("/myapp", "/Environment/one/two/three");
		SearchRequest searchRequest = factory.deriveSearchRequest(request);
		Assert.assertTrue(searchRequest != null);
		Assert.assertTrue("Environment".equals(searchRequest.getTopic()));
		
		CompositeCriteria searchCriteria = searchRequest.getSearchCriteria();
		Assert.assertTrue(searchCriteria != null);
		Assert.assertTrue(CompositeCriteria.Operator.AND.equals(searchCriteria.getOperator()));
		Assert.assertTrue(searchCriteria.getSearchCriteria() != null);
		Assert.assertTrue(searchCriteria.getSearchCriteria().length==3);
		
		testCriteria(searchCriteria.getSearchCriteria()[0], "TABLE_CAT", FilterCriteria.Operation.EQUAL, "one");
		testCriteria(searchCriteria.getSearchCriteria()[1], "TABLE_SCHEM", FilterCriteria.Operation.EQUAL, "two");
		testCriteria(searchCriteria.getSearchCriteria()[2], "TABLE_NAME", FilterCriteria.Operation.EQUAL, "three");
		
		request.setUri("/myapp", "/Environments/one");
		searchRequest = factory.deriveSearchRequest(request);
		Assert.assertTrue(searchRequest != null);
		Assert.assertTrue("Environment".equals(searchRequest.getTopic()));
		
		request.setUri("/myapp", "/Environments/one");
		request.getParameterMap().put(RequestConstants.PARM_START_ROW, "2");
		searchRequest = factory.deriveSearchRequest(request);
		Assert.assertTrue(searchRequest != null);
		Assert.assertTrue(searchRequest.getStartRow().equals(2L));
		
		request.getParameterMap().put(RequestConstants.PARM_START_ROW, "invalid");
		testException("Invalid start row request parm");
		request.getParameterMap().remove(RequestConstants.PARM_START_ROW);
		
		request.setUri("/myapp", "/Environments/one");
		request.getParameterMap().put(RequestConstants.PARM_MAX_ROWS, "2");
		searchRequest = factory.deriveSearchRequest(request);
		Assert.assertTrue(searchRequest != null);
		Assert.assertTrue(searchRequest.getMaxRows().equals(2L));
		
		request.getParameterMap().put(RequestConstants.PARM_MAX_ROWS, "invalid");
		testException("Invalid max rows request parm");
		request.getParameterMap().remove(RequestConstants.PARM_MAX_ROWS);

	}
	
	private void testCriteria(Criteria criteria, String fieldName, FilterCriteria.Operation operator, Object value) {
		Assert.assertTrue(criteria instanceof FilterCriteria);
		FilterCriteria filterCriteria = (FilterCriteria)criteria;
		
		Assert.assertTrue(fieldName.equals(filterCriteria.getFieldName()));
		Assert.assertTrue(operator.equals(filterCriteria.getOperation()));
		Assert.assertTrue(value.equals(filterCriteria.getValue()));
	}

	private void testException(String testMessage) {
		Throwable exceptionThrown=null;
		try {factory.deriveSearchRequest(request);}
		catch (Exception e) {
			exceptionThrown=e;
		}
		Assert.assertTrue(exceptionThrown != null);
		Assert.assertTrue(exceptionThrown.getMessage() != null);
		Assert.assertTrue(exceptionThrown.getMessage().contains(testMessage));
	}
	
	@Test
	public void testDeriveSearachNodes() throws Exception {
		request.setUri("/myapp", "/Environment/one/two/three");
		Assert.assertTrue(Arrays.deepEquals(factory.deriveSearchNodes(request), 
				StringUtils.split("/Environment/one/two/three", '/')));
		
		request.setUri("", "/moneta/Environment");
		Assert.assertTrue(Arrays.deepEquals(factory.deriveSearchNodes(request), 
				StringUtils.split("/moneta/Environment", '/')));
		
		MonetaEnvironment.getConfiguration().setIgnoredContextPathNodes(new String[]{"moneta"});
		Assert.assertTrue(Arrays.deepEquals(factory.deriveSearchNodes(request), 
				StringUtils.split("/Environment", '/')));
	}

}
