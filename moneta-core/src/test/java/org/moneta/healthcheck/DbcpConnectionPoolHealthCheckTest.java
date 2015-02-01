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
package org.moneta.healthcheck;

import java.util.Properties;

import javax.management.ObjectName;

import org.apache.commons.dbcp2.ConnectionFactory;
import org.apache.commons.dbcp2.DriverManagerConnectionFactory;
import org.apache.commons.dbcp2.PoolableConnection;
import org.apache.commons.dbcp2.PoolableConnectionFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.codahale.metrics.health.HealthCheck.Result;


public class DbcpConnectionPoolHealthCheckTest {
	
	private static final String VALIDATION_SQL = "SELECT CURRENT_DATE AS today, CURRENT_TIME AS now FROM (VALUES(0))";
	DbcpConnectionPoolHealthCheck healthCheck;
	GenericObjectPool<PoolableConnection> connectionPool;
	PoolableConnectionFactory poolableConnectionFactory;

	@Before
	public void setUp() throws Exception {
		org.hsqldb.jdbc.JDBCDriver.class.newInstance();
		Properties connectionProps=new Properties();
		connectionProps.put("user", "SA");
		connectionProps.put("password", "");
		connectionProps.put("create", "true");
		
		ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(
				"jdbc:hsqldb:mem:my-sample",connectionProps);
		
		ObjectName poolName= poolName=new ObjectName("org.moneta", "connectionPool", "TestPool");
		poolableConnectionFactory=
				new PoolableConnectionFactory(connectionFactory,poolName);
		poolableConnectionFactory.setDefaultCatalog("PUBLIC");
		poolableConnectionFactory.setValidationQuery(VALIDATION_SQL);
		
		connectionPool = 
				new GenericObjectPool<PoolableConnection>(poolableConnectionFactory);
		poolableConnectionFactory.setPool(connectionPool);
		connectionPool.setMaxTotal(2);
		connectionPool.setMaxWaitMillis(400);
		
		healthCheck = new DbcpConnectionPoolHealthCheck(connectionPool, "mine");
	}
	
	@Test
	public void testBasic() throws Exception {
		Assert.assertTrue(healthCheck.execute().equals(Result.healthy()));
		Assert.assertTrue(connectionPool.getNumActive()==0);
		
		poolableConnectionFactory.setValidationQuery("crap");		
		Result testResult = healthCheck.execute();
		Assert.assertTrue(!testResult.isHealthy());
		Assert.assertTrue(testResult.getMessage() != null);
		Assert.assertTrue(testResult.getMessage().contains("validation error"));
		poolableConnectionFactory.setValidationQuery(VALIDATION_SQL);
		
		healthCheck.setMaxWaitingConnections(-1);
		testResult = healthCheck.execute();
		Assert.assertTrue(!testResult.isHealthy());
		Assert.assertTrue(testResult.getMessage() != null);
		Assert.assertTrue(testResult.getMessage().contains("Overloaded connection pool"));
		Assert.assertTrue(healthCheck.getMaxWaitingConnections() == -1);
	}

	@Test
	public void testExceptions() throws Exception {
		Throwable exceptionThrown = null;
		try {new DbcpConnectionPoolHealthCheck(null, null);}
		catch (Exception e) {
			exceptionThrown = e;
		}
		Assert.assertTrue(exceptionThrown != null);
		Assert.assertTrue(exceptionThrown.getMessage() != null);
		Assert.assertTrue(exceptionThrown.getMessage().contains("connection"));
		
		exceptionThrown = null;
		try {new DbcpConnectionPoolHealthCheck(null, null);}
		catch (Exception e) {
			exceptionThrown = e;
		}
		Assert.assertTrue(exceptionThrown != null);
		Assert.assertTrue(exceptionThrown.getMessage() != null);
		Assert.assertTrue(exceptionThrown.getMessage().contains("connection"));
	}
	

}
