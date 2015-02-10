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
package org.moneta.utils;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 * Generic REST service testing utilities.
 * @author D. Ashmore
 *
 */
public class RestTestingUtils {
	
	public static HttpResponse simpleRESTGet(String requestUri) {
		Validate.notEmpty(requestUri, "Null or blank requestUri not allowed.");
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(requestUri);
		try {
			return httpclient.execute(httpGet);
		} catch (Exception e) {
			throw new ContextedRuntimeException(e)
				.addContextValue("requestUri", requestUri);
		} 
	}
	
//	public static HttpResponse simpleRESTPost(String requestUri, String postData) {
//		Validate.notEmpty(requestUri, "Null or blank requestUri not allowed.");
//		CloseableHttpClient httpclient = HttpClients.createDefault();
//		HttpPost httpPost = new HttpPost(requestUri);
//		httpPost.set
//		try {
//			return httpclient.execute(httpPost);
//		} catch (Exception e) {
//			throw new ContextedRuntimeException(e)
//				.addContextValue("requestUri", requestUri);
//		} 
//	}

}
