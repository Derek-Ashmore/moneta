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

import org.apache.commons.lang3.Validate;
import org.moneta.error.MonetaException;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Generic Json utilities
 * @author D. Ashmore
 *
 */
public class JsonUtils {
	
	/**
	 * Will convert the given object to Json format.
	 */
	public static String serialize(Object jsonObject) {
		Validate.notNull(jsonObject, "Null object cannot be converted to Json");
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(jsonObject);
		} catch (Exception e) {
			throw new MonetaException("Error converting object to Json", e)
			.addContextValue("object", jsonObject); 
		} 
	}

}
