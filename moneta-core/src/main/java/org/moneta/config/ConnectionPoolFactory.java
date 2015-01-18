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

import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.dbcp2.ConnectionFactory;
import org.apache.commons.dbcp2.DriverManagerConnectionFactory;
import org.apache.commons.dbcp2.PoolableConnection;
import org.apache.commons.dbcp2.PoolableConnectionFactory;
import org.apache.commons.lang.Validate;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.moneta.error.MonetaException;
import org.moneta.types.topic.MonetaDataSource;

/**
 * Is able to convert a JDBC connect pool specification into a connection pool
 * @author D. Ashmore
 *
 */
class ConnectionPoolFactory {
	
	public static ObjectPool<PoolableConnection> createConnectionPool(MonetaDataSource dataSourceType) {
		Validate.notNull(dataSourceType, "Null MonetaDataSource not allowed.");
		
		Validate.notEmpty(dataSourceType.getDataSourceName(), "Null or blank name not allowed");
		Validate.notEmpty(dataSourceType.getConnectionUrl(), "Null or blank url not allowed");
		Validate.notNull(dataSourceType.getDriver(), "Null driver not allowed");
		
		try {
			dataSourceType.getDriver().newInstance();
		} catch (Exception e) {
			throw new MonetaException("Data source JDBC driver can't be instantiated", e)
			.addContextValue("JDBC Driver class", dataSourceType.getDriver().getName());
		} 
		
		Properties connectionProps = new Properties();
		connectionProps.putAll(dataSourceType.getJdbcConnectionProperties());
		ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(
				dataSourceType.getConnectionUrl(),connectionProps);
		
		ObjectName poolName= null;
		try {
			poolName=new ObjectName("org.moneta", "connectionPool", dataSourceType.getDataSourceName());
		} catch (MalformedObjectNameException e) {
			throw new MonetaException("Data source name not valid", e)
			.addContextValue("Data source name", dataSourceType.getDataSourceName());
		}
		PoolableConnectionFactory poolableConnectionFactory=
				new PoolableConnectionFactory(connectionFactory,poolName);
		
		Set<String> unusedConnectionPoolPropSet = new HashSet<String>();
		Map<String, String> propMap = new HashMap<String,String>(dataSourceType.getConnectionPoolProperties());
		
		PropertyDescriptor pDesc = null;		
		for (String propName: propMap.keySet()) {
			pDesc = assignProperty(dataSourceType.getDataSourceName(), poolableConnectionFactory,
					propName, propMap.get(propName));
			if (pDesc == null) {
				unusedConnectionPoolPropSet.add(propName);
			}
		}
		
		GenericObjectPool<PoolableConnection> pool = 
				new GenericObjectPool<PoolableConnection>(poolableConnectionFactory);
		
		for (String propName: new HashSet<String>(unusedConnectionPoolPropSet)) {
			pDesc = assignProperty(dataSourceType.getDataSourceName(), pool,
					propName, propMap.get(propName));
			if (pDesc != null) {
				unusedConnectionPoolPropSet.remove(propName);
			}
		}
		
		if (unusedConnectionPoolPropSet.size() > 0) {
			MonetaException me = new MonetaException("Invalid connection pool properties detected");
			for (String propName: unusedConnectionPoolPropSet) {
				me.addContextValue("pool property name", propName);
			}
			
			throw me;
		}
		return pool;
	}

	protected static PropertyDescriptor assignProperty(
			String dataSourceName,
			Object poolRelatedBean,
			String propName,
			String propValue) {
		PropertyDescriptor pDesc = null;
		try {
			pDesc = PropertyUtils.getPropertyDescriptor(poolRelatedBean, propName);
		} catch (Exception e) {
			throw new MonetaException("Connection pool property not valid")
			.addContextValue("Data source name", dataSourceName)
			.addContextValue("Property name", propName);
		} 
		
		if (pDesc == null) {
			return null;
		}
		
		try {
			PropertyUtils.setProperty(poolRelatedBean, 
					propName, 
					ValueNormalizationUtil.convertString(
							pDesc.getPropertyType(), 
							propValue));
		} catch (Exception e) {
			throw new MonetaException("Error setting connection pool property", e)
			.addContextValue("Data source name", dataSourceName)
			.addContextValue("Property name", propName);
		}
		return pDesc;
	}


}
