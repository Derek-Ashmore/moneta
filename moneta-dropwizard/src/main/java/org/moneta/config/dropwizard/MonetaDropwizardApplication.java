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
package org.moneta.config.dropwizard;

import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import net.admin4j.ui.servlets.MemoryMonitorStartupServlet;
import net.admin4j.ui.servlets.ThreadMonitorStartupServlet;

import org.eclipse.jetty.servlet.BaseHolder.Source;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.Holder;
import org.eclipse.jetty.servlet.ServletHolder;
import org.force66.correlate.RequestCorrelationFilter;
import org.moneta.MonetaPerformanceFilter;
import org.moneta.MonetaServlet;
import org.moneta.MonetaTopicListServlet;
import org.moneta.config.MonetaConfiguration;

import com.codahale.metrics.JmxReporter;

/**
 * Dropwizard configuration for Moneta
 * @author D. Ashmore
 *
 */
public class MonetaDropwizardApplication extends
		Application<MonetaDropwizardConfiguration> {
	
	public static void main(String[] args) throws Exception {
        new MonetaDropwizardApplication().run(args);
    }

	@Override
	public void run(MonetaDropwizardConfiguration configuration,
			Environment environment) throws Exception {
		
		/*
		 * The ServletHolder allows you to specify init parameters and other servlet configuration 
		 * itmes in the web.xml.  Setting the order means that the servlet is initialized
		 * on startup; by default it is not.
		 */
		ServletHolder topicHolder = new ServletHolder(Source.EMBEDDED);
        topicHolder.setHeldClass(MonetaServlet.class);
        topicHolder.setInitOrder(0);
        topicHolder.setInitParameter(MonetaServlet.CONFIG_IGNORED_CONTEXT_PATH_NODES, 
        		"moneta,topic");
        environment.getApplicationContext()
        	.getServletHandler()
        	.addServletWithMapping(topicHolder,"/moneta/topic/*");

        //  Will be initialized on first use by default.
		environment.getApplicationContext().addServlet(
				MonetaTopicListServlet.class, "/moneta/topics/*");
		
		/*
		 * Install thread contention monitoring -- withdrawn after issue with Jetty discovered.
		 */
//		ServletHolder threadContentionHolder = new ServletHolder(Source.EMBEDDED);
//		threadContentionHolder.setHeldClass(ThreadMonitorStartupServlet.class);
//		threadContentionHolder.setInitOrder(0);
//		environment.getApplicationContext()
//	    	.getServletHandler()
//	    	.addServlet(threadContentionHolder);
		
		/*
		 * Install memory alert monitoring
		 */
		ServletHolder memoryAlertHolder = new ServletHolder(Source.EMBEDDED);
		memoryAlertHolder.setHeldClass(MemoryMonitorStartupServlet.class);
		memoryAlertHolder.setInitOrder(0);
		environment.getApplicationContext()
	    	.getServletHandler()
	    	.addServlet(memoryAlertHolder);
		
		/*
		 * Install the performance filter
		 */
		FilterHolder perfFilterHolder = new FilterHolder(Holder.Source.EMBEDDED);
		perfFilterHolder.setHeldClass(MonetaPerformanceFilter.class);
		perfFilterHolder.setInitParameter(MonetaPerformanceFilter.PARM_MAX_TRNASACTION_TIME_THRESHOLD_IN_MILLIS, "3000");
		environment.getApplicationContext().addFilter(perfFilterHolder, 
				"/moneta/*", null);
		
		/*
		 * Install RequestCorrelation filter so I can get a correlation id in the logs
		 */
		FilterHolder correlationFilterHolder = new FilterHolder(Holder.Source.EMBEDDED);
		correlationFilterHolder.setHeldClass(RequestCorrelationFilter.class);
		
		// Install healthchecks
		MonetaConfiguration config = new MonetaConfiguration();
		for (String checkName: config.getHealthChecks().keySet()) {
			environment.healthChecks().register(checkName, config.getHealthChecks().get(checkName));
		}
		
		final JmxReporter jmxReporter = JmxReporter.forRegistry(environment.metrics()).build();
		jmxReporter.start();
	}

}
