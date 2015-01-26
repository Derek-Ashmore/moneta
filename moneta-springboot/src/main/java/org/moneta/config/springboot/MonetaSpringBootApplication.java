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

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.moneta.MonetaServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.DispatcherServlet;

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
	
	public MonetaSpringBootApplication() {
		super();
		System.out.println("MonetaSpringBootApplication was instantiated");
	}

	public static void main(String[] args) {
		ApplicationContext ctx = SpringApplication.run(MonetaSpringBootApplication.class, args);
		
		
		System.out.println("MonetaSpringBootApplication main() has been run");

//        String[] beanNames = ctx.getBeanDefinitionNames();
//        Arrays.sort(beanNames);
//        for (String beanName : beanNames) {
//            System.out.println(beanName);
//        }

	}

	@Override
	public void onStartup(ServletContext servletContext)
			throws ServletException {
		System.out.println("Registering the MonetaServlet and added mapping /moneta/*");
		servletContext.addServlet("MonetaServlet", MonetaServlet.class);
		servletContext.getServletRegistration("MonetaServlet").addMapping("/moneta/*");
		System.out.println("MonetaServlet registration done!");
		super.onStartup(servletContext);
	}

}
