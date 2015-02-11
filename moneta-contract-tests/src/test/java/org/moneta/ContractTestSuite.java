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
import org.apache.http.HttpResponse;
import org.junit.Assert;
import org.moneta.utils.RestTestingUtils;

public class ContractTestSuite {
	
	private String urlPrefix;
	
	public ContractTestSuite(String urlPrefix) {
		this.setUrlPrefix(urlPrefix);
	}

	public String getUrlPrefix() {
		return urlPrefix;
	}

	public void setUrlPrefix(String urlPrefix) {
		this.urlPrefix = urlPrefix;
	}
	
	public void testTopicsBasic() throws Exception {
		HttpResponse response = RestTestingUtils.simpleRESTGet(this.urlPrefix+"topics");
		System.out.println(IOUtils.toString(response.getEntity().getContent()));
		Assert.assertTrue(response.getStatusLine().getStatusCode() == 200);	
	}
	
	public void testEnvironmentBasic() throws Exception {
		HttpResponse response = RestTestingUtils.simpleRESTGet(this.urlPrefix+"topic/Environment");
		System.out.println(IOUtils.toString(response.getEntity().getContent()));
		Assert.assertTrue(response.getStatusLine().getStatusCode() == 200);
		
		response = RestTestingUtils.simpleRESTGet(this.urlPrefix+"topic/Environments");
		System.out.println(IOUtils.toString(response.getEntity().getContent()));
		Assert.assertTrue(response.getStatusLine().getStatusCode() == 200);
	}

}
