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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;

import org.apache.commons.collections4.iterators.IteratorEnumeration;

public class MockRequest implements HttpServletRequest {
    
    Map<String,Object> attributeMap = new HashMap<String,Object>();
    Map<String,String> parameterMap = new HashMap<String,String>();
    
    HttpSession session = new MockSession();
    String requestURI = "/myApp/jmu";
    String pathInfo = "/jmu";
    String contextPath = "/myApp";
    String method = "get";
    
    public void setUri(String contextPath, String pathInfo) {
    	this.contextPath = prependHash(contextPath);
    	this.pathInfo = prependHash(pathInfo);
    	requestURI = this.contextPath + this.pathInfo;
    }
    
    private String prependHash(String value) {
    	if (value == null)   return "/";
    	if (value != null && value.startsWith("/"))   return value;
    	return "/"+value;
    }

    public Object getAttribute(String key) {
        return attributeMap.get(key);
    }
    
    public Enumeration getAttributeNames() {
        // TODO Auto-generated method stub
        return new IteratorEnumeration(attributeMap.keySet().iterator());
    }

    public String getCharacterEncoding() {
        // TODO Auto-generated method stub
        return null;
    }

    public int getContentLength() {
        // TODO Auto-generated method stub
        return 0;
    }

    public String getContentType() {
        // TODO Auto-generated method stub
        return null;
    }

    public ServletInputStream getInputStream() throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    public String getLocalAddr() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getLocalName() {
        // TODO Auto-generated method stub
        return null;
    }

    public int getLocalPort() {
        // TODO Auto-generated method stub
        return 0;
    }

    public Locale getLocale() {
        // TODO Auto-generated method stub
        return null;
    }

    public Enumeration getLocales() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getParameter(String arg0) {
        return this.parameterMap.get(arg0);
    }

    public Map getParameterMap() {
        return this.parameterMap;
    }

    public Enumeration getParameterNames() {
        return new IteratorEnumeration(this.parameterMap.keySet().iterator());
    }

    public String[] getParameterValues(String arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public String getProtocol() {
        // TODO Auto-generated method stub
        return null;
    }

    public BufferedReader getReader() throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    public String getRealPath(String arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public String getRemoteAddr() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getRemoteHost() {
        // TODO Auto-generated method stub
        return null;
    }

    public int getRemotePort() {
        // TODO Auto-generated method stub
        return 0;
    }

    public RequestDispatcher getRequestDispatcher(String arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public String getScheme() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getServerName() {
        // TODO Auto-generated method stub
        return null;
    }

    public int getServerPort() {
        // TODO Auto-generated method stub
        return 0;
    }

    public boolean isSecure() {
        // TODO Auto-generated method stub
        return false;
    }

    public void removeAttribute(String key) {
        attributeMap.remove(key);

    }

    public void setAttribute(String key, Object value) {
        attributeMap.put(key, value);

    }

    public void setCharacterEncoding(String arg0)
            throws UnsupportedEncodingException {
        // TODO Auto-generated method stub

    }

    public String getAuthType() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getContextPath() {
        return contextPath;
    }

    public Cookie[] getCookies() {
        // TODO Auto-generated method stub
        return null;
    }

    public long getDateHeader(String arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    public String getHeader(String arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public Enumeration getHeaderNames() {
        // TODO Auto-generated method stub
        return null;
    }

    public Enumeration getHeaders(String arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public int getIntHeader(String arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    public String getMethod() {
        return method;
    }
    
    public void setMethod(String method) {
        this.method = method;
    }

    public String getPathInfo() {
        return pathInfo;
    }

    public String getPathTranslated() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getQueryString() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getRemoteUser() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getRequestURI() {
        // TODO Auto-generated method stub
        return requestURI;
    }

    public StringBuffer getRequestURL() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getRequestedSessionId() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getServletPath() {
        // TODO Auto-generated method stub
        return null;
    }

    public HttpSession getSession() {
        // TODO Auto-generated method stub
        return session;
    }

    public HttpSession getSession(boolean arg0) {
        // TODO Auto-generated method stub
        return session;
    }

    public Principal getUserPrincipal() {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean isRequestedSessionIdFromCookie() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isRequestedSessionIdFromURL() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isRequestedSessionIdFromUrl() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isRequestedSessionIdValid() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isUserInRole(String arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    public void setRequestURI(String requestURI) {
        this.requestURI = requestURI;
    }

	public long getContentLengthLong() {
		// TODO Auto-generated method stub
		return 0;
	}

	public ServletContext getServletContext() {
		// TODO Auto-generated method stub
		return null;
	}

	public AsyncContext startAsync() throws IllegalStateException {
		// TODO Auto-generated method stub
		return null;
	}

	public AsyncContext startAsync(ServletRequest servletRequest,
			ServletResponse servletResponse) throws IllegalStateException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isAsyncStarted() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isAsyncSupported() {
		// TODO Auto-generated method stub
		return false;
	}

	public AsyncContext getAsyncContext() {
		// TODO Auto-generated method stub
		return null;
	}

	public DispatcherType getDispatcherType() {
		// TODO Auto-generated method stub
		return null;
	}

	public String changeSessionId() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean authenticate(HttpServletResponse response)
			throws IOException, ServletException {
		// TODO Auto-generated method stub
		return false;
	}

	public void login(String username, String password) throws ServletException {
		// TODO Auto-generated method stub
		
	}

	public void logout() throws ServletException {
		// TODO Auto-generated method stub
		
	}

	public Collection<Part> getParts() throws IOException, ServletException {
		// TODO Auto-generated method stub
		return null;
	}

	public Part getPart(String name) throws IOException, ServletException {
		// TODO Auto-generated method stub
		return null;
	}

	public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass)
			throws IOException, ServletException {
		// TODO Auto-generated method stub
		return null;
	}

	public void setPathInfo(String pathInfo) {
		this.pathInfo = pathInfo;
	}

}
