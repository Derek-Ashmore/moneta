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
package org.moneta.config;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;

import org.apache.commons.configuration.XMLConfiguration;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.moneta.types.topic.MonetaDataSource;
import org.moneta.types.topic.Topic;

public class MonetaConfigurationTest {
	
	public static final String CONFIG_TEST_FILE_NAME = "src/test/resources/maneta.xml";
	File testFile;

	@Before
	public void setUp() throws Exception {
		testFile = new File(CONFIG_TEST_FILE_NAME);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testLoadConfigurationFromFile() throws Exception {
		XMLConfiguration config = MonetaConfiguration.loadConfigurationFromFile(testFile.getAbsolutePath());
		Assert.assertTrue(config != null);
		
		Throwable exceptionThrown = null;
		try {config = MonetaConfiguration.loadConfigurationFromFile("non-existent.xml");}
		catch (Exception e) {
			exceptionThrown = e; 
		}
		Assert.assertTrue(exceptionThrown != null);
		
	}
	
	@Test
	public void testLoadConfigurationFromStream() throws Exception {
		XMLConfiguration config = MonetaConfiguration.loadConfigurationFromStream(new FileInputStream(testFile.getAbsolutePath()));
		Assert.assertTrue(config != null);
		
		Throwable exceptionThrown = null;
		try {config = MonetaConfiguration.loadConfigurationFromStream(null);}
		catch (Exception e) {
			exceptionThrown = e; 
		}
		Assert.assertTrue(exceptionThrown != null);
		
	}
	
	@Test
	public void testStreamConstructor() throws Exception {
		MonetaConfiguration config = new MonetaConfiguration(new FileInputStream(testFile.getAbsolutePath()));
		Connection conn = config.getConnection("InMemoryDb");
		Assert.assertTrue(conn != null);
		
		Topic topic = config.getTopic("Environment");
		Assert.assertTrue(topic != null);
		
		MonetaDataSource source = config.getMonetaDataSource("InMemoryDb");
		Assert.assertTrue(source != null);
	}

}
