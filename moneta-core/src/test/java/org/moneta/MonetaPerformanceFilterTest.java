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
import java.io.PrintStream;

import net.admin4j.timer.TaskTimer;
import net.admin4j.timer.TaskTimerFactory;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.moneta.config.MonetaConfiguration;
import org.moneta.config.MonetaEnvironment;

public class MonetaPerformanceFilterTest {
	
	MonetaPerformanceFilter filter;
	MockFilterConfig filterConfig;
	MockFilterChain filterChain;
	MockRequest request;
	MockResponse response;

	@Before
	public void setUp() throws Exception {
		filter = new MonetaPerformanceFilter();
		filterConfig = new MockFilterConfig();
		
		filterConfig.addInitParameter("notifier", "net.admin4j.util.notify.LogNotifier");
		
		filterChain = new MockFilterChain();
		
		request = new MockRequest();
		response = new MockResponse();
		
		MonetaEnvironment.setConfiguration(new MonetaConfiguration());
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testInit() throws Exception {
		filter.init(filterConfig);
		Assert.assertTrue(filter.getTransactionTimeThreshold() == null);
		
		PrintStream stdErr = System.err;
		
		ByteArrayOutputStream stdErrStream = new ByteArrayOutputStream();
		System.setErr(new PrintStream(stdErrStream));
		
		filterConfig.addInitParameter(
				MonetaPerformanceFilter.PARM_MAX_TRNASACTION_TIME_THRESHOLD_IN_MILLIS, 
				"10crap");
		filter.init(filterConfig);
		Assert.assertTrue(stdErrStream.size() > 0);
		Assert.assertTrue(stdErrStream.toString().contains("NumberFormatException"));
		Assert.assertTrue(stdErrStream.toString().contains("10crap"));
		
		System.setErr(stdErr);
		
		filterConfig.addInitParameter(
				MonetaPerformanceFilter.PARM_MAX_TRNASACTION_TIME_THRESHOLD_IN_MILLIS, 
				"10");
		filter.init(filterConfig);
		Assert.assertTrue(filter.getTransactionTimeThreshold().equals(10L));
		
		
	}
	
	@Test
	public void testFilter() throws Exception {
		TaskTimer perfMonitor;
		request.setUri("/myapp", null);
		filter.doFilter(request, response, filterChain);
//		testResponse("Search topic not provided");
		
		request.setUri("/myapp", "/topics");
		filter.doFilter(request, response, filterChain);
		
		TaskTimerFactory.delete("Environment");
		request.setUri("/myapp", "/Environment/one/two/three");
		filter.doFilter(request, response, filterChain);
		Assert.assertTrue(TaskTimerFactory.getDataSummaryMap().containsKey("Environment"));
		
		
		PrintStream stdErr = System.err;
		
		ByteArrayOutputStream stdErrStream = new ByteArrayOutputStream();
		System.setErr(new PrintStream(stdErrStream));
		
		filterConfig.addInitParameter(
				MonetaPerformanceFilter.PARM_MAX_TRNASACTION_TIME_THRESHOLD_IN_MILLIS, 
				"10");
		filter.init(filterConfig);
		filterChain.setSleepTimeInMillis(12);
		TaskTimerFactory.delete("Environment");
		filter.doFilter(request, response, filterChain);
		Assert.assertTrue(TaskTimerFactory.getDataSummaryMap().containsKey("Environment"));
		Assert.assertTrue(stdErrStream.toString().contains("Transaction longer than threshold"));
		Assert.assertTrue(stdErrStream.toString().contains("12"));
		
		System.setErr(stdErr);
	}
	
	private void testResponse(String testMessage) {
		Throwable exceptionThrown=null;
		response = new MockResponse();
		try {filter.doFilter(request, response, filterChain);}
		catch (Exception e) {
			exceptionThrown=e;
		}
		Assert.assertTrue(exceptionThrown == null);
		Assert.assertTrue(response.getMockServletOutputStream().getBytes() != null);
		String responseStr = new String(response.getMockServletOutputStream().getBytes());
		Assert.assertTrue(responseStr.contains(testMessage));
	}
	
	@Test
	public void testDeriveTimerLabel() throws Exception {
		request.setUri("/myapp", null);
		Assert.assertTrue("/myapp/".equals(filter.deriveTimerLabel(request)));
		
		request.setUri(null, null);
		Assert.assertTrue("/".equals(filter.deriveTimerLabel(request)));
		
		request.setUri("/myapp", "/topics");
		Assert.assertTrue("/myapp/topics".equals(filter.deriveTimerLabel(request)));
	}

}
