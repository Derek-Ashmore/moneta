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

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.moneta.config.MonetaEnvironment;
import org.moneta.error.MonetaException;
import org.moneta.types.search.CompositeCriteria;
import org.moneta.types.search.Criteria;
import org.moneta.types.search.FilterCriteria;
import org.moneta.types.search.SearchRequest;
import org.moneta.types.topic.Topic;
import org.moneta.types.topic.TopicKeyField;

class SearchRequestFactory {
	
	public SearchRequest deriveSearchRequest(HttpServletRequest request) {
		String[] uriNodes = deriveSearachNodes(request);
		if (ArrayUtils.isEmpty(uriNodes) )  {
			throw new MonetaException("Search topic not provided in request uri")
				.addContextValue("request path info", request.getPathInfo())
				.addContextValue("request context path", request.getContextPath())
				.addContextValue("request uri", request.getRequestURI());
		}
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setTopic(uriNodes[0]);
		CompositeCriteria baseCriteria = new CompositeCriteria();
		searchRequest.setSearchCriteria(baseCriteria);
		
		baseCriteria.setOperator(CompositeCriteria.Operator.AND);
		baseCriteria.setSearchCriteria(new FilterCriteria[0]);
		FilterCriteria filterCriteria = null;
		TopicKeyField keyField = null;
		
		// TODO put in logic for request parms startRow, maxRows, and returnFields
		
		Topic searchTopic = MonetaEnvironment.getConfiguration().getTopic(searchRequest.getTopic());
		if (searchTopic==null) {
			throw new MonetaException("Topic not configured")
			.addContextValue("topic", searchRequest.getTopic());
		}
		
		for (int pathParamOffset = 1; pathParamOffset < uriNodes.length; pathParamOffset++) {
			if (searchTopic.getKeyFieldList().size() < pathParamOffset) {
				throw new MonetaException("Search key in request uri not configured for topic")
				.addContextValue("search key", uriNodes[pathParamOffset])
				.addContextValue("topic", searchRequest.getTopic())
				.addContextValue("request path info", request.getPathInfo())
				.addContextValue("request context path", request.getContextPath())
				.addContextValue("request uri", request.getRequestURI());
			}
			
			keyField=searchTopic.getKeyFieldList().get(pathParamOffset-1);
			filterCriteria = new FilterCriteria();
			filterCriteria.setFieldName(keyField.getColumnName());
			filterCriteria.setOperation(FilterCriteria.Operation.EQUAL);
			if (TopicKeyField.DataType.STRING.equals(keyField.getDataType())) {
				filterCriteria.setValue(uriNodes[pathParamOffset]);
			}
			else {
				// TODO  Need to do a better job of normalizing numeric keys and error handling
				filterCriteria.setValue(Long.valueOf(uriNodes[pathParamOffset]));
			}
			
			baseCriteria.setSearchCriteria( (Criteria[])ArrayUtils.add(baseCriteria.getSearchCriteria(), filterCriteria));
		}
		
		// TODO Put in logic for detecting search criteria 

		return searchRequest;
	}

	protected String[] deriveSearachNodes(HttpServletRequest request) {
		String searchUri;
		if (request.getContextPath() != null) {
			searchUri = request.getRequestURI().substring(request.getContextPath().length());
		}
		else {
			searchUri = request.getRequestURI();
		}
		String[] nodes= StringUtils.split(searchUri, '/');
		
		if (nodes != null && nodes.length > 0 && 
				MonetaEnvironment.getConfiguration().getIgnoredContextPathNodes() != null) {
			while (ArrayUtils.contains(MonetaEnvironment.getConfiguration().getIgnoredContextPathNodes(), nodes[0])) {
				nodes=(String[])ArrayUtils.remove(nodes, 0);
			}
		}
		
		return nodes;
	}

}
