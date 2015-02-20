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
package org.moneta.config.web;

import javax.servlet.annotation.WebListener;

import org.moneta.config.MonetaConfiguration;
import org.moneta.config.MonetaEnvironment;

import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.servlets.HealthCheckServlet;

@WebListener
public class HealthCheckContextListener extends HealthCheckServlet.ContextListener {

	public static final HealthCheckRegistry HEALTH_CHECK_REGISTRY = new HealthCheckRegistry();
	
	static {
		MonetaConfiguration config = new MonetaConfiguration();
		for (String checkName: config.getHealthChecks().keySet()) {
			HEALTH_CHECK_REGISTRY.register(checkName, config.getHealthChecks().get(checkName));
		}
	}

    @Override
    protected HealthCheckRegistry getHealthCheckRegistry() {
        return HEALTH_CHECK_REGISTRY;
    }
}
