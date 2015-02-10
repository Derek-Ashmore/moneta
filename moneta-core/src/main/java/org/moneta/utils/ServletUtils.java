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

import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.moneta.error.MonetaException;
import org.moneta.types.search.SearchResult;

/**
 * Generic utilities for Moneta Servlets
 * @author D. Ashmore
 *
 */
public class ServletUtils {

	public static void writeResult(SearchResult result, OutputStream out) {
		try {
			IOUtils.write(JsonUtils.serialize(result), out);
			out.flush();
		} catch (Exception e) {
			throw new MonetaException("Error writing result output.", e)
				.addContextValue("result", result);
		}
	}

	public static void writeError(Integer errorCode, Exception error, OutputStream out) {
		SearchResult result = new SearchResult();
		result.setErrorCode(errorCode);
		result.setErrorMessage(ExceptionUtils.getStackTrace(error));
		writeResult(result, out);
	}

}
