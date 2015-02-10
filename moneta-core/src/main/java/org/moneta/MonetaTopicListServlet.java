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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.moneta.types.search.SearchResult;
import org.moneta.utils.ServletUtils;

/**
 * List information about configured topics for Moneta
 * @author D. Ashmore
 *
 */
public class MonetaTopicListServlet extends HttpServlet {

	private static final long serialVersionUID = 4405159464697763008L;
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Implement security check
		
		SearchResult searchResult = null;
		response.setContentType("text/json");
		
		try{
			searchResult = new Moneta().findAllTopics();
			ServletUtils.writeResult(searchResult, response.getOutputStream());
		}
		catch (Exception e) {
			response.setStatus(500);
			ServletUtils.writeError(500, e, response.getOutputStream());
		}
						
		IOUtils.closeQuietly(response.getOutputStream());
	}

}
