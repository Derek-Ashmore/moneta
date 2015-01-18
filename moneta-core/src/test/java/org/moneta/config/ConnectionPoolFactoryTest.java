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
import java.sql.Connection;

import org.apache.commons.dbcp2.PoolableConnection;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.pool2.ObjectPool;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.moneta.types.topic.MonetaDataSource;

public class ConnectionPoolFactoryTest {
	
	MonetaDataSource dataSource;

	@Before
	public void setUp() throws Exception {
		dataSource = new MonetaDataSource();
		dataSource.setDriver(org.hsqldb.jdbc.JDBCDriver.class);
		dataSource.setConnectionUrl("jdbc:hsqldb:mem:my-sample");
		dataSource.setDataSourceName("derekDataSource");
		
		dataSource.getJdbcConnectionProperties().put("user", "SA");
		dataSource.getJdbcConnectionProperties().put("password", "");
		dataSource.getJdbcConnectionProperties().put("create", "true");
		
		dataSource.getConnectionPoolProperties().put("defaultAutoCommit", "false");
		dataSource.getConnectionPoolProperties().put("defaultReadOnly", "false");
		dataSource.getConnectionPoolProperties().put("defaultTransactionIsolation", "2");
		dataSource.getConnectionPoolProperties().put("defaultCatalog", "PUBLIC");
		dataSource.getConnectionPoolProperties().put("cacheState", "false");
		
//		dataSource.getConnectionPoolProperties().put("initialSize", "1");
		dataSource.getConnectionPoolProperties().put("maxTotal", "5");
		dataSource.getConnectionPoolProperties().put("maxIdle", "4");
		dataSource.getConnectionPoolProperties().put("minIdle", "1");
		dataSource.getConnectionPoolProperties().put("maxWaitMillis", "-1");
		
		dataSource.getConnectionPoolProperties().put("validationQuery", "SELECT CURRENT_DATE AS today, CURRENT_TIME AS now FROM (VALUES(0))");
		dataSource.getConnectionPoolProperties().put("testOnCreate", "false");
		dataSource.getConnectionPoolProperties().put("testOnBorrow", "true");
		dataSource.getConnectionPoolProperties().put("testOnReturn", "false");
		dataSource.getConnectionPoolProperties().put("testWhileIdle", "false");
		
		dataSource.getConnectionPoolProperties().put("timeBetweenEvictionRunsMillis", "-1");
		dataSource.getConnectionPoolProperties().put("numTestsPerEvictionRun", "3");
		dataSource.getConnectionPoolProperties().put("minEvictableIdleTimeMillis", "1800000");
//		dataSource.getConnectionPoolProperties().put("softMiniEvictableIdleTimeMillis", "-1");
		dataSource.getConnectionPoolProperties().put("maxConnLifetimeMillis", "-1");
		
		dataSource.getConnectionPoolProperties().put("connectionInitSql", null);
		dataSource.getConnectionPoolProperties().put("lifo", "true");
		dataSource.getConnectionPoolProperties().put("poolStatements", "false");
		dataSource.getConnectionPoolProperties().put("maxOpenPrepatedStatements", "-1");

	}

	@Test
	public void testBasicHappyPath() throws Exception {
		ObjectPool<PoolableConnection> pool = ConnectionPoolFactory.createConnectionPool(dataSource);
		Assert.assertTrue(pool != null);
		
		Connection conn = pool.borrowObject();
		Assert.assertTrue(conn != null);
		Assert.assertTrue(!conn.isClosed() );
	}
	
	@Test
	public void testAssignProperty() throws Exception {
		PropertyDescriptor pDesc;
		pDesc = ConnectionPoolFactory.assignProperty("foo", dataSource, "dataSourceName", "foo");
		Assert.assertTrue(pDesc != null);
		Assert.assertTrue("dataSourceName".equals(pDesc.getName()));
		Assert.assertTrue("foo".equals(dataSource.getDataSourceName()));
		
		Throwable exceptionThrown = null;
		try {
			ConnectionPoolFactory.assignProperty("foo", dataSource, null, "foo");
		}
		catch (Exception e) {
			exceptionThrown = e;
		}
		Assert.assertTrue(exceptionThrown != null);
		Assert.assertTrue(exceptionThrown.getMessage().contains("not valid"));
		
		pDesc = ConnectionPoolFactory.assignProperty("foo", dataSource, "crap", "foo");
		Assert.assertTrue(pDesc == null);
		
		exceptionThrown = null;
		try {
			ConnectionPoolFactory.assignProperty("foo", dataSource, "jdbcConnectionProperties", "foo");
		}
		catch (Exception e) {
			exceptionThrown = e;
		}
		
		Assert.assertTrue(exceptionThrown != null);
		String trace = ExceptionUtils.getStackTrace(exceptionThrown);
		Assert.assertTrue(trace.contains("java.util.Map"));
	}

}
