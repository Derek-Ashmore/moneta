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

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public class MockResponse implements HttpServletResponse {
    
    private String contentType;
    private ServletOutputStream outputStream = new MockServletOutputStream();
    
    public MockServletOutputStream getMockServletOutputStream() {
        return (MockServletOutputStream)outputStream;
    }

    public void flushBuffer() throws IOException {
        // TODO Auto-generated method stub

    }

    public int getBufferSize() {
        // TODO Auto-generated method stub
        return 0;
    }

    public String getCharacterEncoding() {
        // TODO Auto-generated method stub
        return Charset.defaultCharset().name();
    }

    public String getContentType() {
         return contentType;
    }

    public Locale getLocale() {
        // TODO Auto-generated method stub
        return null;
    }

    public ServletOutputStream getOutputStream() throws IOException {
        return outputStream;
    }

    public PrintWriter getWriter() throws IOException {
        // TODO Auto-generated method stub
        return new PrintWriter(new OutputStreamWriter(outputStream));
    }

    public boolean isCommitted() {
        // TODO Auto-generated method stub
        return false;
    }

    public void reset() {
        // TODO Auto-generated method stub

    }

    public void resetBuffer() {
        // TODO Auto-generated method stub

    }

    public void setBufferSize(int arg0) {
        // TODO Auto-generated method stub

    }

    public void setCharacterEncoding(String arg0) {
        // TODO Auto-generated method stub

    }

    public void setContentLength(int arg0) {
        // TODO Auto-generated method stub

    }

    public void setContentType(String contentType) {
        this.contentType = contentType;

    }

    public void setLocale(Locale arg0) {
        // TODO Auto-generated method stub

    }

    public void addCookie(Cookie arg0) {
        // TODO Auto-generated method stub

    }

    public void addDateHeader(String arg0, long arg1) {
        // TODO Auto-generated method stub

    }

    public void addHeader(String arg0, String arg1) {
        // TODO Auto-generated method stub

    }

    public void addIntHeader(String arg0, int arg1) {
        // TODO Auto-generated method stub

    }

    public boolean containsHeader(String arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    public String encodeRedirectURL(String arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public String encodeRedirectUrl(String arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public String encodeURL(String arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public String encodeUrl(String arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public void sendError(int arg0) throws IOException {
        // TODO Auto-generated method stub

    }

    public void sendError(int arg0, String arg1) throws IOException {
        // TODO Auto-generated method stub

    }

    public void sendRedirect(String arg0) throws IOException {
        // TODO Auto-generated method stub

    }

    public void setDateHeader(String arg0, long arg1) {
        // TODO Auto-generated method stub

    }

    public void setHeader(String arg0, String arg1) {
        // TODO Auto-generated method stub

    }

    public void setIntHeader(String arg0, int arg1) {
        // TODO Auto-generated method stub

    }

    public void setStatus(int arg0) {
        // TODO Auto-generated method stub

    }

    public void setStatus(int arg0, String arg1) {
        // TODO Auto-generated method stub

    }

	public void setContentLengthLong(long len) {
		// TODO Auto-generated method stub
		
	}

	public int getStatus() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getHeader(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection<String> getHeaders(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection<String> getHeaderNames() {
		// TODO Auto-generated method stub
		return null;
	}

}
