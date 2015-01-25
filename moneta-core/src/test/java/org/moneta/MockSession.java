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

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

import org.apache.commons.collections4.iterators.IteratorEnumeration;



public class MockSession implements HttpSession {
    
    Map<String,Object> attributeMap = new HashMap<String,Object>();
    Map<String,Object> valueMap = new HashMap<String,Object>();

    public Object getAttribute(String attr) {
        return attributeMap.get(attr);
    }

    public Enumeration getAttributeNames() {
        // TODO Auto-generated method stub
        return new IteratorEnumeration(attributeMap.keySet().iterator());
    }

    public long getCreationTime() {
        // TODO Auto-generated method stub
        return 0;
    }

    public String getId() {
        // TODO Auto-generated method stub
        return null;
    }

    public long getLastAccessedTime() {
        // TODO Auto-generated method stub
        return 0;
    }

    public int getMaxInactiveInterval() {
        // TODO Auto-generated method stub
        return 0;
    }

    public ServletContext getServletContext() {
        // TODO Auto-generated method stub
        return null;
    }

    public HttpSessionContext getSessionContext() {
        // TODO Auto-generated method stub
        return null;
    }

    public Object getValue(String key) {
        return valueMap.get(key);
    }

    public String[] getValueNames() {
        return valueMap.keySet().toArray(new String[0]);
    }

    public void invalidate() {
        // TODO Auto-generated method stub

    }

    public boolean isNew() {
        // TODO Auto-generated method stub
        return false;
    }

    public void putValue(String key, Object value) {
        valueMap.put(key, value);

    }

    public void removeAttribute(String key) {
        attributeMap.remove(key);

    }

    public void removeValue(String key) {
        valueMap.remove(key);

    }

    public void setAttribute(String key, Object value) {
        attributeMap.put(key, value);

    }

    public void setMaxInactiveInterval(int arg0) {
        // TODO Auto-generated method stub

    }

}
