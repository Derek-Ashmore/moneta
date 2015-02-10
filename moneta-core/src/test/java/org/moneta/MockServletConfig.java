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
package org.moneta;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import org.apache.commons.lang.StringUtils;

public class MockServletConfig implements ServletConfig {
	
	private String servletName;
	private Map<String,String> initParmMap = new HashMap<String,String>();

	public String getServletName() {
		return servletName;
	}

	public ServletContext getServletContext() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getInitParameter(String name) {
		return initParmMap.get(name);
	}

	public Enumeration<String> getInitParameterNames() {

		return new StringEnumeration(initParmMap.keySet().toArray(new String[0]));
	}
	
	public static class StringEnumeration implements Enumeration<String> {
		String[] strArray;
		int offset=0;
		
		public StringEnumeration(String[] array) {
			strArray=array;
		}

		public boolean hasMoreElements() {
			// TODO Auto-generated method stub
			return offset<strArray.length;
		}

		public String nextElement() {
			return strArray[offset++];
		}
		
	}

	public Map<String, String> getInitParmMap() {
		return initParmMap;
	}

	public void setServletName(String servletName) {
		this.servletName = servletName;
	}

}
