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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.moneta.MonetaTestBase;
import org.moneta.types.search.SearchRequest;
import org.moneta.types.search.SearchResult;

public class MonetaSearchDAOTest extends MonetaTestBase {
	
	MonetaSearchDAO dao;
	SearchRequest searchRequest;
	
	@Before
	public void setUp() throws Exception {
		super.setUp();
		dao = new MonetaSearchDAO();
		searchRequest = new SearchRequest();		
		searchRequest.setTopic("Environment");
	}

	@Test
	public void testBasic() throws Exception {
		SearchResult result = dao.find(searchRequest);
		Assert.assertTrue(result != null);
		Assert.assertTrue(result.getResultData().length == 92);
		
		searchRequest.setStartRow(90L);
		result = dao.find(searchRequest);
		Assert.assertTrue(result != null);
		Assert.assertTrue(result.getResultData().length == 3);
		
		searchRequest.setStartRow(null);
		searchRequest.setMaxRows(10L);
		result = dao.find(searchRequest);
		Assert.assertTrue(result != null);
		Assert.assertTrue(result.getResultData().length == 10);
	}

}
