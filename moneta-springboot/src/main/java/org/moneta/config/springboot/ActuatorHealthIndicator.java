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

import java.util.Map;

import org.moneta.config.MonetaEnvironment;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import com.codahale.metrics.health.HealthCheck;
import com.codahale.metrics.health.HealthCheckRegistry;

/**
 * Moneta has internal healthchecks written with codehale Metrics.  This class
 * allows those health checks to run under spring-boot.
 * @author D. Ashmore
 *
 */
@Component
public class ActuatorHealthIndicator implements HealthIndicator {
	
	private static HealthCheckRegistry healthCheckRegistry;

	public Health health() {
		
		Map<String, HealthCheck.Result> resultMap = healthCheckRegistry.runHealthChecks();
		HealthCheck.Result result;
		for (String checkName: resultMap.keySet()) {
			result = resultMap.get(checkName);
			if ( !result.isHealthy()) {
				return Health.down().withDetail(checkName, result.toString()).build();
			}
		}
		
		return Health.up().build();
	}
	
	protected static HealthCheckRegistry getHealthCheckRegistry() {
		return healthCheckRegistry;
	}

	protected static void setHealthCheckRegistry(
			HealthCheckRegistry healthCheckRegistry) {
		ActuatorHealthIndicator.healthCheckRegistry = healthCheckRegistry;
	}


}
