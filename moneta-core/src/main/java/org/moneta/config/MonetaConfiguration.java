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

import java.sql.Connection;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.pool2.ObjectPool;
import org.moneta.error.MonetaException;
import org.moneta.types.topic.MonetaDataSource;

/**
 * Utility class to find and establish Moneta application configuration
 * @author D. Ashmore
 *
 */
public class MonetaConfiguration {
	
	public static final String DEFAULT_CONFIGURATION_FILE_NAME="moneta.xml";
	
	private static final Map<String,ObjectPool> connectionPoolMap = new HashMap<String,ObjectPool>();
	
	static {
		init();
	}

	protected static void init() {
		String configFileName = System.getProperty("moneta.configuration");
		XMLConfiguration config = null;
		if (StringUtils.isEmpty(configFileName)) {
			try {
				config = new XMLConfiguration(configFileName);
			} catch (ConfigurationException e) {
				throw new MonetaException("Moneta configuration file not loaded", e)
				.addContextValue("configFileName", configFileName);
			}
		}
		
		config = new XMLConfiguration();
		try {
			config.load(MonetaConfiguration.class.getClassLoader().getResourceAsStream(DEFAULT_CONFIGURATION_FILE_NAME));
		} catch (ConfigurationException e) {
			throw new MonetaException("Moneta configuration file not loaded from classpath", e)
				.addContextValue("configFileName", DEFAULT_CONFIGURATION_FILE_NAME);
		}
		
		int nbrDataSources = 0;
		Object temp = config.getList("dataSources.dataSource.name");
		if (temp instanceof Collection) {
			nbrDataSources = ((Collection)temp).size();
		}
		
		
		MonetaDataSource dataSourceType;
		String driverClassName;
		Class driverClass;
		for (int i = 0; i < nbrDataSources; i++) {
			dataSourceType = new MonetaDataSource();
			dataSourceType.setDataSourceName(config.getString("dataSources.dataSource(" + i + ").name"));
			dataSourceType.setConnectionUrl(config.getString("dataSources.dataSource(" + i + ").url"));
			driverClassName = config.getString("dataSources.dataSource(" + i + ").driver");
			
			Validate.notEmpty(dataSourceType.getDataSourceName(), "Null or blank dataSources.dataSource.name not allowed");
			Validate.notEmpty(dataSourceType.getConnectionUrl(), "Null or blank dataSources.dataSource.url not allowed");
			Validate.notEmpty(driverClassName, "Null or blank dataSources.dataSource.driver not allowed");
			
			try {
				driverClass = Class.forName(driverClassName);
			} catch (ClassNotFoundException e) {
				throw new MonetaException("Data source JDBC driver not found in classpath", e)
				.addContextValue("dataSources.dataSource.driver", driverClassName)
				.addContextValue("Data Source offset", i);
			}
			dataSourceType.setDriver( driverClass);
			
			connectionPoolMap.put(dataSourceType.getDataSourceName(), 
					ConnectionPoolFactory.createConnectionPool(dataSourceType));

		}
	}
	
	public static Connection getConnection(String topicName) {
		Validate.notEmpty(topicName, "Null or blank topicName not allowed");
		ObjectPool connectionPool = connectionPoolMap.get(topicName);
		if (connectionPool == null) {
			throw new MonetaException("Data Source Not Found")
				.addContextValue("topicName", topicName);
		}
		
		try {
			return (Connection)connectionPool.borrowObject();
		} catch (Exception e) {
			throw new MonetaException("Error creating JDBC connection")
				.addContextValue("topicName", topicName);
		}
	}
	

}
