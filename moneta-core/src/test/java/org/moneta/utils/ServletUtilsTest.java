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

import java.io.ByteArrayOutputStream;

import org.junit.Assert;
import org.junit.Test;
import org.moneta.error.MonetaException;

public class ServletUtilsTest {

	@Test
	public void testWriteResult() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		Throwable exceptionThrown = null;
		try {ServletUtils.writeResult(null, out);}
		catch (Exception e) {
			exceptionThrown=e;
		}
		Assert.assertTrue(exceptionThrown != null);
		Assert.assertTrue(exceptionThrown.getMessage() != null);
	}
	
	@Test
	public void testWriteError() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Exception error = new MonetaException("Outer exception message", new IllegalArgumentException("Inner exception message"))
			.addContextValue("context", "contextValue");
		ServletUtils.writeError(427, error, out);
		
		Assert.assertTrue(out.toString().contains("Outer exception message"));
		Assert.assertTrue(out.toString().contains("Inner exception message"));
		Assert.assertTrue(out.toString().contains("contextValue"));
	}

}
