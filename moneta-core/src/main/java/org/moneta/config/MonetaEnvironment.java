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

import org.moneta.types.BaseType;

public class MonetaEnvironment extends BaseType {
	
	private static MonetaConfiguration configuration;

	public static MonetaConfiguration getConfiguration() {
		return configuration;
	}

	public static void setConfiguration(MonetaConfiguration configuration) {
		MonetaEnvironment.configuration = configuration;
	}

}
