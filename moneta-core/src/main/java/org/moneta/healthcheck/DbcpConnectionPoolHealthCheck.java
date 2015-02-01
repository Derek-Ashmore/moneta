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

import org.apache.commons.dbcp2.PoolableConnection;
import org.apache.commons.dbcp2.PoolableConnectionFactory;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.pool2.impl.GenericObjectPool;

import com.codahale.metrics.health.HealthCheck;

/**
 * Will check the health of a database connection pool managed by Apache Commons
 * DBCP. Items checked are the following: <li>The number of threads waiting for
 * a connection isn't larger than a configurable threashold (default=3)</li> <li>
 * A connection from the pool does sucessfully validate.</li>
 * 
 * @author D. Ashmore
 *
 */
public class DbcpConnectionPoolHealthCheck extends HealthCheck {
	private final GenericObjectPool<PoolableConnection> connectionPool;
	private int maxWaitingConnections = 3;
	private final String poolName;

	public DbcpConnectionPoolHealthCheck(
			GenericObjectPool<PoolableConnection> pool, String poolName) {
		Validate.notNull(pool, "Null connection pool not allowed.");
		Validate.notEmpty(poolName, "Null or blank poolName not allowed.");
		Validate.isTrue(pool.getFactory() instanceof PoolableConnectionFactory,
				"this check only handles connection pools using PoolableConnectionFactory");
		connectionPool = pool;
		this.poolName = poolName;
		this.setMaxWaitingConnections(3);
	}

	@Override
	protected Result check() throws Exception {

		GenericObjectPool<PoolableConnection> pool = (GenericObjectPool<PoolableConnection>) connectionPool;
		if (pool.getNumWaiters() > maxWaitingConnections) {
			return Result.unhealthy("Overloaded connection pool.  name="
					+ poolName + " nbrWaiters=" + pool.getNumWaiters());
		}

		PoolableConnectionFactory poolFactory = (PoolableConnectionFactory) pool
				.getFactory();
		PoolableConnection conn = null;
		try {
			conn = pool.borrowObject();
			poolFactory.validateConnection(conn);
		} catch (Exception e) {
			return Result
					.unhealthy("Database connection validation error.  error="
							+ ExceptionUtils.getStackTrace(e));
		} finally {
			DbUtils.closeQuietly(conn);
		}

		return Result.healthy();
	}

	public int getMaxWaitingConnections() {
		return maxWaitingConnections;
	}

	public void setMaxWaitingConnections(int maxWaitingConnections) {
		this.maxWaitingConnections = maxWaitingConnections;
	}

}
