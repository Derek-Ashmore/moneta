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

import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

public class ValueNormalizationUtilTest {

	@Test
	public void testBasicHappyPath() {
		Assert.assertTrue(ValueNormalizationUtil.convertString(Integer.class, null) == null);
		Assert.assertTrue(ValueNormalizationUtil.convertString(String.class, "foo").equals("foo"));
		Assert.assertTrue(ValueNormalizationUtil.convertString(Integer.class, "5").equals(Integer.valueOf(5)));
		Assert.assertTrue(ValueNormalizationUtil.convertString(Long.class, "5").equals(Long.valueOf(5)));
		Assert.assertTrue(ValueNormalizationUtil.convertString(int.class, "5").equals(Integer.valueOf(5)));
		Assert.assertTrue(ValueNormalizationUtil.convertString(long.class, "5").equals(Long.valueOf(5)));
		Assert.assertTrue(ValueNormalizationUtil.convertString(Boolean.class, "true").equals(Boolean.TRUE));
	}
	
	@Test
	public void testExceptions() {
		boolean exceptionThrown = false;
		
		try {ValueNormalizationUtil.convertString(null, null);}
		catch (Exception e) {
			exceptionThrown = true;
		}
		Assert.assertTrue(exceptionThrown);
		
		exceptionThrown = false;
		try {ValueNormalizationUtil.convertString(Collection.class, "foo");}
		catch (Exception e) {
			exceptionThrown = true;
		}
		Assert.assertTrue(exceptionThrown);
	}

}
