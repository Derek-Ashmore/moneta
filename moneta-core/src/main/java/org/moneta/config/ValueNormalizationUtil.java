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

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang3.BooleanUtils;
import org.moneta.error.MonetaException;

/**
 * Converter utility for Strings
 * @author D. Ashmore
 *
 */
class ValueNormalizationUtil {

	/**
	 * Will convert a String into the specified property type.  Integer, Long, Boolean, and String supported.
	 * @param targetType
	 * @param value
	 * @return convertedValue
	 */
	public static Object convertString(Class targetType, String value) {
		Validate.notNull(targetType, "Null targetType not allowed.");
		if (value == null) {
			return value;
		}
		if (ClassUtils.isAssignable(targetType, String.class)) {
			return value;
		}
		if (ClassUtils.isAssignable(targetType, Integer.class)) {
			return Integer.valueOf(value);
		}
		if (ClassUtils.isAssignable(targetType, int.class)) {
			return Integer.valueOf(value);
		}
		if (ClassUtils.isAssignable(targetType, Long.class)) {
			return Long.valueOf(value);
		}
		if (ClassUtils.isAssignable(targetType, long.class)) {
			return Long.valueOf(value);
		}
		Boolean bValue = BooleanUtils.toBooleanObject(value);
		if (bValue != null) {
			return bValue;
		}
		
		throw new MonetaException("Property type not supported")
			.addContextValue("targetType", targetType.getName());
	}

}
