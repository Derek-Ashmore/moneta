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
package org.moneta.config.springboot;

import org.moneta.MonetaPerformanceFilter;
import org.moneta.MonetaServlet;
import org.moneta.MonetaTopicListServlet;
import org.moneta.config.MonetaConfiguration;
import org.moneta.config.MonetaEnvironment;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;

/**
 * Spring boot application for Moneta
 * @author D. Ashmore
 *
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan("org.moneta")
@Component
public class MonetaSpringBootApplication extends SpringBootServletInitializer  {
	
	public static void main(String[] args) {
		SpringApplication.run(MonetaSpringBootApplication.class, args);
		
		MonetaConfiguration config = new MonetaConfiguration();
		HealthCheckRegistry registry = new HealthCheckRegistry();
		for (String checkName: MonetaEnvironment.getConfiguration().getHealthChecks().keySet()) {
			registry.register(checkName, MonetaEnvironment.getConfiguration().getHealthChecks().get(checkName));
		}
		ActuatorHealthIndicator.setHealthCheckRegistry(registry);
		
		MetricRegistry metricRegistry = new MetricRegistry();
		final JmxReporter jmxReporter = JmxReporter.forRegistry(metricRegistry).build();
		jmxReporter.start();
	}
	
	@Bean
	public ServletRegistrationBean monetaServlet() {
	    ServletRegistrationBean registration = 
	    		new ServletRegistrationBean(new MonetaServlet(),
	    				"/moneta/topic/*"); 
	    registration.addInitParameter(
	    		MonetaServlet.CONFIG_IGNORED_CONTEXT_PATH_NODES, "moneta,topic");
	    return registration;
	}
	
	@Bean
	public ServletRegistrationBean monetaTopicListServlet() {
	    ServletRegistrationBean registration = 
	    		new ServletRegistrationBean(new MonetaTopicListServlet(), 
	    				"/moneta/topics/*"); 
	    return registration;
	}
	
	@Bean
	public FilterRegistrationBean monetaPerformanceFilter() {
		FilterRegistrationBean registration = 
				new FilterRegistrationBean(new MonetaPerformanceFilter(), 
						monetaServlet(), monetaTopicListServlet());
		return registration;
	}

}
