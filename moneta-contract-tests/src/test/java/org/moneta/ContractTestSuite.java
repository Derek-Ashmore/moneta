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
	
	private String appUrlPrefix;
	private String serviceUrlPrefix;
	private String healthCheckEndpoint;
	private SearchResult result;
	private String jsonContent;
	
	public ContractTestSuite(String appUrlPrefix, String servicePrefix, String healthCheckEndpoint) {
		this.setAppUrlPrefix(appUrlPrefix);
		this.setServiceUrlPrefix(servicePrefix);
		this.setHealthCheckEndpoint(healthCheckEndpoint);
	}

	public String getAppUrlPrefix() {
		return appUrlPrefix;
	}

	public void setAppUrlPrefix(String urlPrefix) {
		this.appUrlPrefix = urlPrefix;
	}
	
	@Test
	public void testTopicsBasic() throws Exception {
		HttpResponse response = RestTestingUtils.simpleRESTGet(this.appUrlPrefix+"topics");
		this.testForOkResult(response, 1, 5);
	}
	
	@Test
	public void testEnvironmentBasic() throws Exception {
		HttpResponse response = RestTestingUtils.simpleRESTGet(this.appUrlPrefix+"topic/Environment");
		this.testForOkResult(response, 92, 13);
		
		response = RestTestingUtils.simpleRESTGet(this.appUrlPrefix+"topic/Environments");
		this.testForOkResult(response, 92, 13);
		
		response = RestTestingUtils.simpleRESTGet(this.appUrlPrefix+"topic/Environments?startRow=90");
		this.testForOkResult(response, 3, 13);
		
		response = RestTestingUtils.simpleRESTGet(this.appUrlPrefix+"topic/Environments?maxRows=10");
		this.testForOkResult(response, 10, 13);
	}
	
	@Test
	public void testHealthcheck() throws Exception {
		String checkUrl = this.serviceUrlPrefix+this.getHealthCheckEndpoint();
		System.out.println("Healthcheck url: " +checkUrl);
		HttpResponse response = RestTestingUtils.simpleRESTGet(checkUrl);
		Assert.assertTrue(response.getStatusLine().getStatusCode() == 200);
		
		System.out.println("Healthcheck output: " +IOUtils.toString(response.getEntity().getContent()));
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

	public String getServiceUrlPrefix() {
		return serviceUrlPrefix;
	}

	public void setServiceUrlPrefix(String servicePrefix) {
		this.serviceUrlPrefix = servicePrefix;
	}

	public String getHealthCheckEndpoint() {
		return healthCheckEndpoint;
	}

	public void setHealthCheckEndpoint(String healthCheckEndpoint) {
		this.healthCheckEndpoint = healthCheckEndpoint;
	}

}
