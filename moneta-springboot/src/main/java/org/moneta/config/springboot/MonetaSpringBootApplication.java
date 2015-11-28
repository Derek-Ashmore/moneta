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

import net.admin4j.ui.servlets.MemoryMonitorStartupServlet;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.force66.correlate.RequestCorrelationFilter;
import org.moneta.MonetaPerformanceFilter;
import org.moneta.MonetaServlet;
import org.moneta.MonetaTopicListServlet;
import org.moneta.config.MonetaConfiguration;
import org.moneta.config.MonetaEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.jetty.JettyServerCustomizer;
import org.springframework.boot.context.properties.ConfigurationProperties;
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
@ConfigurationProperties
@ComponentScan("org.moneta")
@Component
public class MonetaSpringBootApplication extends SpringBootServletInitializer  {

	public static final Integer DEFAULT_SERVER_MAX_THREADS = 20;
	public static final Integer DEFAULT_SERVER_MIN_THREADS = 2;
	public static final Integer DEFAULT_SERVER_IDLE_TIMEOUT = 30;

	private static Logger logger = LoggerFactory.getLogger(MonetaSpringBootApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(MonetaSpringBootApplication.class, args);

		// Find and read application configuration
		MonetaConfiguration config = new MonetaConfiguration();

		// Install all health checks
		HealthCheckRegistry registry = new HealthCheckRegistry();
		for (String checkName : MonetaEnvironment.getConfiguration()
				.getHealthChecks()
				.keySet()) {
			registry.register(checkName, MonetaEnvironment.getConfiguration()
					.getHealthChecks()
					.get(checkName));
		}
		ActuatorHealthIndicator.setHealthCheckRegistry(registry);

		// Install metrics and JMX
		MetricRegistry metricRegistry = new MetricRegistry();
		final JmxReporter jmxReporter = JmxReporter.forRegistry(metricRegistry)
				.build();
		jmxReporter.start();
	}

	@Value("${moneta.server.max.threads}")
	private Integer serverMaxThreads;

	@Value("${moneta.server.min.threads}")
	private Integer serverMinThreads;

	@Value("${moneta.server.idle.timeout}")
	private Integer serverIdleTimeout;

	protected Integer deriveValue(Integer configuredValue, Integer defaultValue) {
		if (configuredValue == null) {
			return defaultValue;
		}
		return configuredValue;
	}

	@Bean
	public ServletRegistrationBean memoryMonitorStartupServlet() {
		ServletRegistrationBean registration =
				new ServletRegistrationBean(new MemoryMonitorStartupServlet(), "/admin4j/memory");
		registration.setLoadOnStartup(1);
		return registration;
	}

	/*
	 * Withdrawn after issue discovered with jetty
	 */
	//	@Bean
	//	public ServletRegistrationBean threadStartupServlet() {
	//	    ServletRegistrationBean registration =
	//	    		new ServletRegistrationBean(new ThreadMonitorStartupServlet(), "/admin4j/threads");
	//	    registration.setLoadOnStartup(1);
	//	    return registration;
	//	}

	@Bean
	public FilterRegistrationBean monetaPerformanceFilter() {
		FilterRegistrationBean registration =
				new FilterRegistrationBean(new MonetaPerformanceFilter(),
						monetaServlet(), monetaTopicListServlet());
		return registration;
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
	public FilterRegistrationBean reportCorrelationFilter() {
		FilterRegistrationBean registration =
				new FilterRegistrationBean(new RequestCorrelationFilter(),
						monetaServlet(), monetaTopicListServlet());
		return registration;
	}

	@Bean
	public EmbeddedServletContainerFactory servletContainer() {
		JettyEmbeddedServletContainerFactory factory = new JettyEmbeddedServletContainerFactory();

		factory.addServerCustomizers(new JettyServerCustomizer() {
			public void customize(final Server server) {
				// Tweak the connection pool used by Jetty to handle incoming
				// HTTP connections
				Integer localServerMaxThreads = deriveValue(serverMaxThreads,
						DEFAULT_SERVER_MAX_THREADS);
				Integer localServerMinThreads = deriveValue(serverMinThreads,
						DEFAULT_SERVER_MIN_THREADS);
				Integer localServerIdleTimeout = deriveValue(serverIdleTimeout,
						DEFAULT_SERVER_IDLE_TIMEOUT);

				logger.info("Container Max Threads={}", localServerMaxThreads);
				logger.info("Container Min Threads={}", localServerMinThreads);
				logger.info("Container Idle Timeout={}", localServerIdleTimeout);

				final QueuedThreadPool threadPool = server.getBean(QueuedThreadPool.class);
				threadPool.setMaxThreads(Integer.valueOf(localServerMaxThreads));
				threadPool.setMinThreads(Integer.valueOf(localServerMinThreads));
				threadPool.setIdleTimeout(Integer.valueOf(localServerIdleTimeout));
			}
		});
		return factory;
	}

}
