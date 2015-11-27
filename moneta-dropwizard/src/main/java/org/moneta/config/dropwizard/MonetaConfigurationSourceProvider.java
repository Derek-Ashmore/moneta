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

import io.dropwizard.configuration.ConfigurationSourceProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang3.Validate;
import org.moneta.error.MonetaException;

/**
 * Allows application configuration via the classpath as well as file reference.
 *
 * @author D. Ashmore
 *
 */
public class MonetaConfigurationSourceProvider implements
ConfigurationSourceProvider {

	public InputStream open(String path) throws IOException {
		Validate.notBlank(path,
				"Null or blank configuration file reference not allowed");

		InputStream configStream = MonetaConfigurationSourceProvider.class.getResourceAsStream(path);
		if (configStream == null) {
			final File file = new File(path);
			if (!file.exists()) {
				throw new MonetaException(
						"configuration not found in the classpath or as file").addContextValue(
								"configuration yaml", path);
			}
			configStream = new FileInputStream(file);
		}
		return configStream;
	}

}
