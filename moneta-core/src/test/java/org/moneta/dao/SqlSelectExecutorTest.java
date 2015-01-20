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

import org.junit.Assert;
import org.junit.Test;
import org.moneta.MonetaTestBase;
import org.moneta.types.search.SearchResult;

public class SqlSelectExecutorTest extends MonetaTestBase {

	@Test
	public void testBasic() throws Exception {
		SqlSelectExecutor exec = new SqlSelectExecutor("Environment", 
				"select * from INFORMATION_SCHEMA.SYSTEM_TABLES");
		SearchResult result = exec.call();
		Assert.assertTrue(result != null);
		Assert.assertTrue(result.getResultData().length == 92);
		
		exec.setStartRow(90L);
		Assert.assertTrue(exec.getStartRow() == 90L);
		result = exec.call();
		Assert.assertTrue(result != null);
		Assert.assertTrue(result.getResultData().length == 3);
		
		exec.setStartRow(null);
		exec.setMaxRows(10L);
		Assert.assertTrue(exec.getMaxRows() == 10L);
		
		result = exec.call();
		Assert.assertTrue(result != null);
		Assert.assertTrue(result.getResultData().length == 10);
	}

}
