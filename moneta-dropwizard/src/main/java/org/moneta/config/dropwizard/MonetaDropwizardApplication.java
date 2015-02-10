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

import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlet.BaseHolder.Source;
import org.moneta.MonetaServlet;
import org.moneta.MonetaTopicListServlet;

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
		ServletHolder holder = new ServletHolder(Source.EMBEDDED);
        holder.setHeldClass(MonetaServlet.class);
        holder.setInitOrder(0);
        holder.setInitParameter(MonetaServlet.CONFIG_IGNORED_CONTEXT_PATH_NODES, "moneta");
        environment.getApplicationContext().getServletHandler().addServletWithMapping(holder,"/moneta/*");

        //  Will be initialized on first use by deftault.
		environment.getApplicationContext().addServlet(MonetaTopicListServlet.class, "/moneta/topics/*");

	}

}
