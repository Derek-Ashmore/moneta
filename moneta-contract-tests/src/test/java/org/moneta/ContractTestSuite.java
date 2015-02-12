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


import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.junit.Assert;
import org.junit.Test;
import org.moneta.types.search.SearchResult;
import org.moneta.utils.RestTestingUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class ContractTestSuite {
	
	private String urlPrefix;
	private SearchResult result;
	private String jsonContent;
	
	public ContractTestSuite(String urlPrefix) {
		this.setUrlPrefix(urlPrefix);
	}

	public String getUrlPrefix() {
		return urlPrefix;
	}

	public void setUrlPrefix(String urlPrefix) {
		this.urlPrefix = urlPrefix;
	}
	
	@Test
	public void testTopicsBasic() throws Exception {
		HttpResponse response = RestTestingUtils.simpleRESTGet(this.urlPrefix+"topics");
		this.testForOkResult(response, 1, 5);
	}
	
	@Test
	public void testEnvironmentBasic() throws Exception {
		HttpResponse response = RestTestingUtils.simpleRESTGet(this.urlPrefix+"topic/Environment");
		this.testForOkResult(response, 92, 13);
		
		response = RestTestingUtils.simpleRESTGet(this.urlPrefix+"topic/Environments");
		this.testForOkResult(response, 92, 13);
		
		response = RestTestingUtils.simpleRESTGet(this.urlPrefix+"topic/Environments?startRow=90");
		this.testForOkResult(response, 3, 13);
		
		response = RestTestingUtils.simpleRESTGet(this.urlPrefix+"topic/Environments?maxRows=10");
		this.testForOkResult(response, 10, 13);
	}
	
	private void testForOkResult(HttpResponse response, int nbrReturnedRecords, int nbrReturnedValues) throws Exception {
		jsonContent = IOUtils.toString(response.getEntity().getContent());
		System.out.println(jsonContent);
		Assert.assertTrue(response.getStatusLine().getStatusCode() == 200);
		
		result = this.toSearchResult(jsonContent);
		Assert.assertTrue(result.getErrorCode() == null);	
		Assert.assertTrue(StringUtils.isEmpty(result.getErrorMessage()));	
		Assert.assertTrue(result.getResultData().length == nbrReturnedRecords);
		Assert.assertTrue(result.getResultData()[0].getValues() != null);
		Assert.assertTrue(result.getResultData()[0].getValues().length == nbrReturnedValues);
	}
	
	private SearchResult toSearchResult(String jsonContent) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(jsonContent, SearchResult.class);
	}

}
