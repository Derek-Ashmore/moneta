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

/**
 * Search criteria specification
 * @author D. Ashmore
 *
 */
public class SearchCriteria extends BaseType implements Criteria {
	
	public static enum Operation {EQUAL, NOT_EQUAL, GREATER_THAN, LESS_THAN, IS_NULL, IS_NOT_NULL, LIKE};
	
	private String fieldName;
	private Operation operation;
	private Object value;
	
	public String getFieldName() {
		return fieldName;
	}
	
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	
	public Operation getOperation() {
		return operation;
	}
	
	public void setOperation(Operation operation) {
		this.operation = operation;
	}
	
	public Object getValue() {
		return value;
	}
	
	public void setValue(Object value) {
		this.value = value;
	}
	

}
