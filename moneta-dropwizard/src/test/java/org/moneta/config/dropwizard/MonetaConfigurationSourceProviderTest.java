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

import net.admin4j.deps.commons.io.IOUtils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MonetaConfigurationSourceProviderTest {

	MonetaConfigurationSourceProvider provider;

	@Before
	public void setUp() throws Exception {
		provider = new MonetaConfigurationSourceProvider();
	}

	@Test
	public void test() throws Exception {
		try {
			provider.open(null);
			Assert.fail();
		} catch (Exception e) {
			Assert.assertTrue(e.getMessage()
					.contains(
							"Null or blank configuration file reference not allowed"));
		}

		try {
			provider.open("fu.bar");
			Assert.fail();
		} catch (Exception e) {
			Assert.assertTrue(e.getMessage()
					.contains("fu.bar"));
		}

		String config = IOUtils.toString(provider.open("src/main/resources/moneta-config.yaml"));
		Assert.assertTrue(config.contains("console"));

		config = IOUtils.toString(provider.open("/moneta-config.yaml"));
		Assert.assertTrue(config.contains("console"));
	}

}
