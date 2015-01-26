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
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.pool2.ObjectPool;
import org.moneta.error.MonetaException;
import org.moneta.types.topic.Dialect;
import org.moneta.types.topic.MonetaDataSource;
import org.moneta.types.topic.Topic;
import org.moneta.types.topic.TopicKeyField;

/**
 * Utility class to find and establish Moneta application configuration
 * @author D. Ashmore
 *
 */
public class MonetaConfiguration {
	
	public static final String MONETA_CONFIGURATION_PROPERTY = "moneta.configuration";
	public static final String DEFAULT_CONFIGURATION_FILE_NAME="moneta.xml";
	
	private final Map<String,MonetaDataSource> dataSourceMap = new HashMap<String,MonetaDataSource>();
	private final Map<String,ObjectPool<PoolableConnection>> connectionPoolMap = new HashMap<String,ObjectPool<PoolableConnection>>();
	private final Map<String,Topic> topicMap = new HashMap<String,Topic>();
	private boolean initRun = false;
	private String[] ignoredContextPathNodes=null;
	
	public MonetaConfiguration() {
		init(findConfiguration());
	}
	public MonetaConfiguration(InputStream configurationStream) {
		init(loadConfigurationFromStream(configurationStream));
	}
	
	protected static final XMLConfiguration findConfiguration() {
		String configFileName = System.getProperty(MONETA_CONFIGURATION_PROPERTY);
		XMLConfiguration config = null;
		if ( !StringUtils.isEmpty(configFileName)) {
			return loadConfigurationFromFile(configFileName);
		}
		
		InputStream configurationStream = MonetaConfiguration.class.getClassLoader().getResourceAsStream(DEFAULT_CONFIGURATION_FILE_NAME);		
		if (configurationStream == null) {
			throw new MonetaException("Moneta configuration not found");
		}
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
		initTopics(config);
		initRun = true;
	}
	
	protected void initDataSources(XMLConfiguration config) {
		int nbrDataSources = 0;
		Object temp = config.getList("DataSources.DataSource[@name]");
		if (temp instanceof Collection) {
			nbrDataSources = ((Collection)temp).size();
		}
		
		
		MonetaDataSource dataSourceType;
		String driverClassName;
		String dialectName;
		Class driverClass;
		for (int i = 0; i < nbrDataSources; i++) {
			dataSourceType = new MonetaDataSource();
			dataSourceType.setDataSourceName(config.getString("DataSources.DataSource(" + i + ")[@name]"));
			dataSourceType.setConnectionUrl(config.getString("DataSources.DataSource(" + i + ")[@url]"));
			driverClassName = config.getString("DataSources.DataSource(" + i + ")[@driver]");
			dialectName = config.getString("DataSources.DataSource(" + i + ")[@dialect]");
			
			Validate.notEmpty(dataSourceType.getDataSourceName(), "Null or blank DataSources.DataSource.name not allowed");
			Validate.notEmpty(dataSourceType.getConnectionUrl(), "Null or blank DataSources.DataSource.url not allowed");
			Validate.notEmpty(driverClassName, "Null or blank DataSources.DataSource.driver not allowed");
			
			try {
				driverClass = Class.forName(driverClassName);
			} catch (ClassNotFoundException e) {
				throw new MonetaException("Data source JDBC driver not found in classpath", e)
				.addContextValue("DataSources.DataSource.driver", driverClassName)
				.addContextValue("Data Source offset", i);
			}
			dataSourceType.setDriver( driverClass);
			
			Dialect dialect;
			String localDialectName;
			if (dialectName != null) {
				localDialectName = dialectName.toUpperCase();
				try {dialect = Dialect.valueOf(localDialectName);}
				catch (Exception e) {
					throw new MonetaException("Dialect not supported", e).addContextValue("dialectName", dialectName);
				}
				dataSourceType.setDialect(dialect);
			}
			
			connectionPoolMap.put(dataSourceType.getDataSourceName(), 
					ConnectionPoolFactory.createConnectionPool(dataSourceType));
			dataSourceMap.put(dataSourceType.getDataSourceName(), dataSourceType);

		}
	}
	
	protected void initTopics(XMLConfiguration config) {
		int nbrTopics = 0;
		Object temp = config.getList("Topics.Topic[@name]");
		if (temp instanceof Collection) {
			nbrTopics = ((Collection)temp).size();
		}
		
		Topic topic;
		String readOnlyStr;
		for (int i = 0; i < nbrTopics; i++) {
			topic = new Topic();
			gatherTopicAttributes(config, topic, i);
			gatherAliasAttributes(config, topic);
			gatherKeyFields(config, topic);
			
			validateTopic(topic);			
			topicMap.put(topic.getTopicName(), topic);
		}
		
		Validate.isTrue(topicMap.size() > 0, "No Topics configured.");	
	}
	
	protected void gatherAliasAttributes(XMLConfiguration config, Topic topic) {
		int nbrAliases = 0;
		Object temp = config.getList("Topics.Topic.Alias[@name]");
		if (temp instanceof Collection) {
			nbrAliases = ((Collection)temp).size();
		}
		
		String name, column;
		for (int i = 0; i < nbrAliases; i++) {
			name=config.getString("Topics.Topic.Alias(" + i + ")[@name]");
			column=config.getString("Topics.Topic.Alias(" + i + ")[@column]");
			if (StringUtils.isEmpty(name) || StringUtils.isEmpty(column)) {
				throw new MonetaException("Topic Alias fields must have both name and column specified")
					.addContextValue("topic", topic.getTopicName())
					.addContextValue("name", name)
					.addContextValue("column", column);
			}
			topic.getAliasMap().put(column, name);
		}
	}
	
	protected void gatherKeyFields(XMLConfiguration config, Topic topic) {
		int nbrKeyFields = 0;
		Object temp = config.getList("Topics.Topic.PrimaryKey.Field[@name]");
		if (temp instanceof Collection) {
			nbrKeyFields = ((Collection)temp).size();
		}
		
		String name, typeStr;
		TopicKeyField.DataType dataType;
		TopicKeyField keyField;
		for (int i = 0; i < nbrKeyFields; i++) {
			name=config.getString("Topics.Topic.PrimaryKey.Field(" + i + ")[@name]");
			typeStr=config.getString("Topics.Topic.PrimaryKey.Field(" + i + ")[@type]");
			if (StringUtils.isEmpty(name) || StringUtils.isEmpty(typeStr)) {
				throw new MonetaException("Topic Primary Key Fields fields must have both name and type specified")
					.addContextValue("topic", topic.getTopicName())
					.addContextValue("name", name)
					.addContextValue("type", typeStr);
			}
			try {dataType = TopicKeyField.DataType.valueOf(typeStr.toUpperCase());}
			catch (Exception e) {
				throw new MonetaException("Datatype not supported", e)
					.addContextValue("topic", topic.getTopicName())
					.addContextValue("key field", name)
					.addContextValue("dataType", typeStr);
			}
			
			keyField = new TopicKeyField();
			topic.getKeyFieldList().add(keyField);
			keyField.setColumnName(name);
			keyField.setDataType(dataType);
		}
	}
	
	protected void gatherTopicAttributes(XMLConfiguration config, Topic topic,
			int i) {
		String readOnlyStr;
		topic.setTopicName(config.getString("Topics.Topic(" + i + ")[@name]"));
		topic.setDataSourceName(config.getString("Topics.Topic(" + i + ")[@dataSource]"));
		topic.setSchemaName(config.getString("Topics.Topic(" + i + ")[@schema]"));
		topic.setCatalogName(config.getString("Topics.Topic(" + i + ")[@catalog]"));
		topic.setTableName(config.getString("Topics.Topic(" + i + ")[@table]"));
		
		readOnlyStr = config.getString("Topics.Topic(" + i + ")[@readOnly]");
		Boolean bValue = BooleanUtils.toBooleanObject(readOnlyStr);
		if (bValue != null)  {
			topic.setReadOnly(bValue);
		}
	}
	protected void validateTopic(Topic topic) {
		Validate.notEmpty(topic.getTopicName(), "Null or blank Topics.Topic.name not allowed");
		Validate.notEmpty(topic.getDataSourceName(), "Null or blank Topics.Topic.dataSource not allowed.  topic="+topic.getTopicName());
		Validate.notEmpty(topic.getTableName(), "Null or blank Topics.Topic.table not allowed.  topic="+topic.getTopicName());
		Validate.notNull(topic.getReadOnly(), "Null or blank Topics.Topic.readOnly not allowed.  topic="+topic.getTopicName());
		
		if (StringUtils.isEmpty(topic.getSchemaName())) {
			Validate.isTrue(topic.getCatalogName()==null, "Null or blank Topics.Topic.catalog not allowed when schema is provided.  topic="+topic.getTopicName());
		}
		
		if ( !connectionPoolMap.containsKey(topic.getDataSourceName())) {
			throw new MonetaException("Topic references non-existent data source")
				.addContextValue("topic", topic.getTopicName())
				.addContextValue("dataSource", topic.getDataSourceName());
		}
	}
	
	/**
	 * Will return a dbConnection for a given information topic.
	 * @param sourceName
	 * @return topicDbConnection
	 */
	public Connection getConnection(String sourceName) {
		Validate.notEmpty(sourceName, "Null or blank sourceName not allowed");
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
	
	public Topic getTopic(String topicName) {
		Validate.notEmpty(topicName, "Null or blank topicName not allowed");
		Validate.isTrue(this.initRun, "Moneta not properly initialized.");
		return topicMap.get(topicName);
	}
	
	public MonetaDataSource getMonetaDataSource(String sourceName) {
		Validate.notEmpty(sourceName, "Null or blank sourceName not allowed");
		Validate.isTrue(this.initRun, "Moneta not properly initialized.");
		return dataSourceMap.get(sourceName);
	}
	public String[] getIgnoredContextPathNodes() {
		return ignoredContextPathNodes;
	}
	public void setIgnoredContextPathNodes(String[] ignoredContextPathNodes) {
		this.ignoredContextPathNodes = ignoredContextPathNodes;
	}
	

}
