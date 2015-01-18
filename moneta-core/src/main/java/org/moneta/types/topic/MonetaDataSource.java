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
package org.moneta.types.topic;

import java.sql.Driver;
import java.util.HashMap;
import java.util.Map;

import org.moneta.types.BaseType;

/**
 * Represents a JDBC data source.
 * @author D. Ashmore
 *
 */
public class MonetaDataSource extends BaseType {
	
	private String dataSourceName;
	private Class<? extends Driver> driver;
	private String connectionUrl;
	private Map<String,String> jdbcConnectionProperties = new HashMap<String,String>();
	private Map<String,String> connectionPoolProperties = new HashMap<String,String>();
	
	public String getDataSourceName() {
		return dataSourceName;
	}
	
	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}
	
	public Class<? extends Driver> getDriver() {
		return driver;
	}
	
	public void setDriver(Class<? extends Driver> driver) {
		this.driver = driver;
	}
	
	public String getConnectionUrl() {
		return connectionUrl;
	}
	
	public void setConnectionUrl(String connectionUrl) {
		this.connectionUrl = connectionUrl;
	}
	
	public Map<String, String> getJdbcConnectionProperties() {
		return jdbcConnectionProperties;
	}

	public Map<String, String> getConnectionPoolProperties() {
		return connectionPoolProperties;
	}


}
