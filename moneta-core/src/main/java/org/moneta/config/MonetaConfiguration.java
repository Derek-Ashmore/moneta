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

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.dbcp2.PoolableConnection;
import org.apache.commons.io.IOUtils;
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
	
	private final Map<String,ObjectPool<PoolableConnection>> connectionPoolMap = new HashMap<String,ObjectPool<PoolableConnection>>();
	private boolean initRun = false;
	
	public MonetaConfiguration() {
		init(findConfiguration());
	}
	public MonetaConfiguration(InputStream configurationStream) {
		init(loadConfigurationFromStream(configurationStream));
	}
	
	protected static final XMLConfiguration findConfiguration() {
		String configFileName = System.getProperty("moneta.configuration");
		XMLConfiguration config = null;
		if ( !StringUtils.isEmpty(configFileName)) {
			return loadConfigurationFromFile(configFileName);
		}
		
		InputStream configurationStream = MonetaConfiguration.class.getClassLoader().getResourceAsStream(DEFAULT_CONFIGURATION_FILE_NAME);		
		config = loadConfigurationFromStream(configurationStream);
		
		return config;
	}

	protected static final XMLConfiguration loadConfigurationFromFile(String configFileName) {
		try {
			return loadConfigurationFromStream(new FileInputStream(configFileName));
		} catch (Exception e) {
			throw new MonetaException("Moneta configuration file not loaded", e)
			.addContextValue("configFileName", configFileName);
		}
	}

	protected static final XMLConfiguration loadConfigurationFromStream(
			InputStream configurationStream) {
		XMLConfiguration config = new XMLConfiguration();
		try {
			config.load(configurationStream);
		} catch (ConfigurationException e) {
			throw new MonetaException("Moneta configuration file not loaded from classpath", e)
				.addContextValue("configFileName", DEFAULT_CONFIGURATION_FILE_NAME);
		}
		finally {
			IOUtils.closeQuietly(configurationStream);
		}
		return config;
	}
	
	/**
	 * Will initialize Moneta given a configuration.  This <b>must</b> be executed before use.
	 * @param config
	 */
	protected final void init(XMLConfiguration config) {
				
		initDataSources(config);		
		initRun = true;
	}
	
	protected void initDataSources(XMLConfiguration config) {
		int nbrDataSources = 0;
		Object temp = config.getList("DataSources.DataSource[@name]");
		if (temp instanceof Collection) {
			nbrDataSources = ((Collection)temp).size();
		}
		
		
		MonetaDataSource DataSourceType;
		String driverClassName;
		Class driverClass;
		for (int i = 0; i < nbrDataSources; i++) {
			DataSourceType = new MonetaDataSource();
			DataSourceType.setDataSourceName(config.getString("DataSources.DataSource(" + i + ")[@name]"));
			DataSourceType.setConnectionUrl(config.getString("DataSources.DataSource(" + i + ")[@url]"));
			driverClassName = config.getString("DataSources.DataSource(" + i + ")[@driver]");
			
			Validate.notEmpty(DataSourceType.getDataSourceName(), "Null or blank DataSources.DataSource.name not allowed");
			Validate.notEmpty(DataSourceType.getConnectionUrl(), "Null or blank DataSources.DataSource.url not allowed");
			Validate.notEmpty(driverClassName, "Null or blank DataSources.DataSource.driver not allowed");
			
			try {
				driverClass = Class.forName(driverClassName);
			} catch (ClassNotFoundException e) {
				throw new MonetaException("Data source JDBC driver not found in classpath", e)
				.addContextValue("DataSources.DataSource.driver", driverClassName)
				.addContextValue("Data Source offset", i);
			}
			DataSourceType.setDriver( driverClass);
			
			connectionPoolMap.put(DataSourceType.getDataSourceName(), 
					ConnectionPoolFactory.createConnectionPool(DataSourceType));

		}
	}
	
	/**
	 * Will return a dbConnection for a given information topic.
	 * @param sourceName
	 * @return topicDbConnection
	 */
	public Connection getConnection(String sourceName) {
		Validate.notEmpty(sourceName, "Null or blank topicName not allowed");
		Validate.isTrue(this.initRun, "Moneta not properly initialized.");
		
		ObjectPool connectionPool = connectionPoolMap.get(sourceName);
		if (connectionPool == null) {
			throw new MonetaException("Data Source Not Found")
				.addContextValue("sourceName", sourceName);
		}
		
		try {
			return (Connection)connectionPool.borrowObject();
		} catch (Exception e) {
			throw new MonetaException("Error creating JDBC connection")
				.addContextValue("sourceName", sourceName);
		}
	}
	

}
