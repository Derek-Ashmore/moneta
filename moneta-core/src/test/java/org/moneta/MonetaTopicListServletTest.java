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

import org.force66.mock.servletapi.MockRequest;
import org.force66.mock.servletapi.MockResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MonetaTopicListServletTest extends MonetaTestBase {

	private MonetaTopicListServlet servlet;
	private MockRequest request;
	private MockResponse response;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		servlet = new MonetaTopicListServlet();
		request = new MockRequest();
		response = new MockResponse();
	}

	@Test
	public void test() throws Exception {
		servlet.doGet(request, response);
		Assert.assertTrue(response.getMockServletOutputStream().getBytes() != null);
		String responseStr = new String(response.getMockServletOutputStream().getBytes());
		Assert.assertTrue(responseStr.contains("Environment"));
	}

}
