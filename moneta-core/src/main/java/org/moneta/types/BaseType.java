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
package org.moneta.types;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Base value object type
 * @author D. Ashmore
 *
 */
abstract class BaseType {

	private static final boolean TEST_TRANSIENTS = false;

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this, TEST_TRANSIENTS);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, TEST_TRANSIENTS);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
