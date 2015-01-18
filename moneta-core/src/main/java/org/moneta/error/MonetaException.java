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
package org.moneta.error;

import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.apache.commons.lang3.exception.ExceptionContext;

/**
 * Application exception class for Moneta
 * @author D. Ashmore
 *
 */
public class MonetaException extends ContextedRuntimeException {

	private static final long serialVersionUID = -682655095144383360L;

	public MonetaException() {
	}

	public MonetaException(String message) {
		super(message);
	}

	public MonetaException(Throwable cause) {
		super(cause);
	}

	public MonetaException(String message, Throwable cause) {
		super(message, cause);
	}

	public MonetaException(String message, Throwable cause,
			ExceptionContext context) {
		super(message, cause, context);
	}

}
