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
import java.io.OutputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.moneta.config.MonetaConfiguration;
import org.moneta.config.MonetaEnvironment;
import org.moneta.error.MonetaException;
import org.moneta.types.search.SearchRequest;
import org.moneta.types.search.SearchResult;
import org.moneta.utils.JsonUtils;

/**
 * Handles all Moneta web requests 
 * @author D. Ashmore
 *
 */
public class MonetaServlet extends HttpServlet {

	private static final long serialVersionUID = 2139138787842502094L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Implement security check
				
		SearchRequest searchRequest = null;
		SearchResult searchResult = null;
		response.setContentType("text/json");
		
		try{
			searchRequest=new SearchRequestFactory().deriveSearchRequest(request);
			searchResult = new Moneta().find(searchRequest);
			this.writeResult(searchResult, response.getOutputStream());
		}
		catch (Exception e) {
			response.setStatus(500);
			this.writeError(500, e, response.getOutputStream());
		}
						
		IOUtils.closeQuietly(response.getOutputStream());
	}
	
	protected void writeError(Integer errorCode, Exception error, OutputStream out) {
		SearchResult result = new SearchResult();
		result.setErrorCode(errorCode);
		result.setErrorMessage(ExceptionUtils.getStackTrace(error));
		this.writeResult(result, out);
	}
	
	protected void writeResult(SearchResult result, OutputStream out) {
		try {
			IOUtils.write(JsonUtils.serialize(result), out);
			out.flush();
		} catch (Exception e) {
			throw new MonetaException("Error writing result output.", e)
				.addContextValue("result", result);
		}
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		MonetaEnvironment.setConfiguration(new MonetaConfiguration());
	}

}
