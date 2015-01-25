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

import org.moneta.dao.MonetaSearchDAO;
import org.moneta.types.search.SearchRequest;
import org.moneta.types.search.SearchResult;

/**
 * Base Moneta class providing search and store capability.
 * @author D. Ashmore
 *
 */
public class Moneta {
	
	public SearchResult find(SearchRequest request) {
		return new MonetaSearchDAO().find(request);
	}
	

}
