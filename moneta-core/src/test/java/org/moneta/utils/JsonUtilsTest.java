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
package org.moneta.utils;

import static org.junit.Assert.fail;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.moneta.types.Record;
import org.moneta.types.Value;
import org.moneta.types.search.SearchResult;

public class JsonUtilsTest {
	
	SearchResult testResult;

	@Before
	public void setUp() throws Exception {
		testResult = new SearchResult();
		testResult.setErrorCode(0);
		testResult.setErrorMessage("hi there");
		
		Record[] records = new Record[2];
		testResult.setResultData(records);
		for (int i = 0; i < 2; i++) {
			records[i] = new Record();
			records[i].setValues(new Value[]{new Value("fi", "fi"), new Value("fo", "fum")});
		}
		
	}

	@Test
	public void testSerialize() throws Exception {
		String jsonBasic = JsonUtils.serialize(testResult);
		System.out.println(jsonBasic);
		Assert.assertTrue(!StringUtils.isEmpty(jsonBasic));
		
		Throwable exceptionThrown = null;
		try {JsonUtils.serialize(null);}
		catch(Exception e) {
			exceptionThrown = e;
		}
		Assert.assertTrue(exceptionThrown != null);
		Assert.assertTrue(!StringUtils.isEmpty(exceptionThrown.getMessage()));
		Assert.assertTrue(exceptionThrown.getMessage().contains("cannot be converted"));
		
		// for Line coverage -:)
		new JsonUtils();

	}

}
