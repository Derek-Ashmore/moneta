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
package org.moneta.dao;

import org.apache.commons.dbutils.QueryRunner;
import org.junit.Assert;
import org.junit.Test;
import org.moneta.HSqlTest;
import org.moneta.types.Record;

public class RecordResultSetHandlerTest extends HSqlTest {
	
	@Test
	public void testBasicHappyPath() throws Exception {
		QueryRunner runner = new QueryRunner();
		Record[] recArray = runner.query(nativeConnection, 
				"select * from INFORMATION_SCHEMA.SYSTEM_TABLES", new RecordResultSetHandler());
		Assert.assertTrue(recArray != null);
		Assert.assertTrue(recArray.length == 92);
	}

}
