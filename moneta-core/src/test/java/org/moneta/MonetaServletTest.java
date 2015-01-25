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

import java.io.ByteArrayOutputStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.moneta.error.MonetaException;

public class MonetaServletTest extends MonetaTestBase {
	
	private MonetaServlet servlet;
	private MockRequest request;
	private MockResponse response;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		servlet = new MonetaServlet();
		request = new MockRequest();
		response = new MockResponse();
	}

	@Test
	public void testDoGet() throws Exception {
		servlet.init(null);
		
		request.setUri("/myapp", null);
		testResponse("Search topic not provided");
		
		request.setUri("/myapp", "/crap");
		testResponse("Topic not configured");
		testResponse("crap");
		
		request.setUri("/myapp", "/Environment/one/too/too/many");
		testResponse("Search key in request uri not configured");
		testResponse("many");
		
		request.setUri("/myapp", "/Environment/one/two/three");
		testResponse("\"records\":[]}");		
		
		request.setUri("/myapp", "/Environment");
		testResponse("one row for each external sequence generator");		
		
		request.setUri("/myapp", "/Environment/PUBLIC/INFORMATION_SCHEMA/ADMINISTRABLE_ROLE_AUTHORIZATIONS");
		testResponse("ADMINISTRABLE_ROLE_AUTHORIZATIONS");
//		System.out.println(response.getMockServletOutputStream().asString());
	}
	
	@Test
	public void testWriteResult() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		Throwable exceptionThrown = null;
		try {servlet.writeResult(null, out);}
		catch (Exception e) {
			exceptionThrown=e;
		}
		Assert.assertTrue(exceptionThrown != null);
		Assert.assertTrue(exceptionThrown.getMessage() != null);
	}
	
	@Test
	public void testWriteError() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Exception error = new MonetaException("Outer exception message", new IllegalArgumentException("Inner exception message"))
			.addContextValue("context", "contextValue");
		servlet.writeError(427, error, out);
		
		Assert.assertTrue(out.toString().contains("Outer exception message"));
		Assert.assertTrue(out.toString().contains("Inner exception message"));
		Assert.assertTrue(out.toString().contains("contextValue"));
	}
	
	private void testResponse(String testMessage) {
		Throwable exceptionThrown=null;
		response = new MockResponse();
		try {servlet.doGet(request, response);}
		catch (Exception e) {
			exceptionThrown=e;
		}
		Assert.assertTrue(exceptionThrown == null);
		Assert.assertTrue(response.getMockServletOutputStream().getBytes() != null);
		String responseStr = new String(response.getMockServletOutputStream().getBytes());
		Assert.assertTrue(responseStr.contains(testMessage));
	}

}
