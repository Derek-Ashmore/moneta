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

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import net.admin4j.timer.TaskTimer;
import net.admin4j.timer.TaskTimerFactory;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.moneta.error.MonetaException;
import org.moneta.types.topic.Topic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Monitors performance for 
 * @author D. Ashmore
 *
 */
public class MonetaPerformanceFilter implements Filter {
	
	public static final String PARM_MAX_TRNASACTION_TIME_THRESHOLD_IN_MILLIS="max.transaction.time.millis";
	
	private Long transactionTimeThreshold = null;
	private static Logger perfLogger = LoggerFactory.getLogger("Performance");
	private static Logger logger = LoggerFactory.getLogger(MonetaPerformanceFilter.class);

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		TaskTimer perfMonitor = null;
		SearchRequestFactory searchRequestFactory = null;
		Topic searchTopic = null;
		
		long startTimeMillis = System.currentTimeMillis();
		try {
			searchRequestFactory = new SearchRequestFactory();
			String[] uriNodes = searchRequestFactory.deriveSearchNodes( (HttpServletRequest)request);
			if ( !ArrayUtils.isEmpty(uriNodes)) {
				searchTopic = searchRequestFactory.findSearchTopic(uriNodes[0], false);
				if (searchTopic == null) {
					perfMonitor = TaskTimerFactory.start( this.deriveTimerLabel((HttpServletRequest)request) );
				}
				else {
					perfMonitor = TaskTimerFactory.start(searchTopic.getTopicName());
				}
			}
			
			chain.doFilter(request, response);
		}
		finally {
			if (perfMonitor != null) perfMonitor.stop();
			
			long elapsedTime = System.currentTimeMillis() - startTimeMillis;
			if (transactionTimeThreshold != null && elapsedTime > transactionTimeThreshold) {
				perfLogger.warn("Transaction longer than threshold.  ElapsedMillis={} contextPath={} request={}", 
						new Object[]{elapsedTime, ((HttpServletRequest)request).getContextPath(),
						((HttpServletRequest)request).getPathInfo()});
			}
		}

	}

	public void destroy() {
		// NoOp	
	}

	public void init(FilterConfig filterConfig) throws ServletException {

		String transTimeStr = filterConfig.getInitParameter(PARM_MAX_TRNASACTION_TIME_THRESHOLD_IN_MILLIS);
		if (StringUtils.isNotBlank(transTimeStr)) {
			try {
				transactionTimeThreshold = Long.valueOf(transTimeStr);
			}
			catch (Exception e) {
				MonetaException ex = (MonetaException)
						new MonetaException("Invalid filter parameter - must be numeric", e)
							.addContextValue("parm name", PARM_MAX_TRNASACTION_TIME_THRESHOLD_IN_MILLIS)
							.addContextValue("parm value", transTimeStr);
				this.logger.warn(ExceptionUtils.getFullStackTrace(e));
			}
		}
	}

	protected Long getTransactionTimeThreshold() {
		return transactionTimeThreshold;
	}
	
	protected String deriveTimerLabel(HttpServletRequest request) {
		StringBuilder builder = new StringBuilder();
		if ( StringUtils.isNotBlank(request.getContextPath())) {
			builder.append(request.getContextPath());
		}
		if ( StringUtils.isNotBlank(request.getPathInfo())) {
			builder.append(request.getPathInfo());
		}
		
		if (builder.length() == 0) {
			return "/";
		}
		
		return builder.toString().replaceAll("//", "/");
	}

}
