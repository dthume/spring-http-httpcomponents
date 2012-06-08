/*
 * #%L
 * Spring HttpComponents
 * %%
 * Copyright (C) 2011 - 2012 David Thomas Hume
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.dthume.spring.http.client.httpcomponents;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;

import java.net.URI;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;

public class HttpComponentsClientRequestFactoryTest {

    private final HttpClient client =
            createMock(HttpClient.class);
    private final HttpResponse response =
            createMock(HttpResponse.class);
    
    private final HttpComponentsClientRequestFactory factory =
            new HttpComponentsClientRequestFactory(client);
    
    @Before
    public void resetMocks() {
        reset(client, response);
    }
    
    private void replayMocks() {
        replay(client, response);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullHttpClient() throws Exception {
        new HttpComponentsClientRequestFactory(null);
    }
    
    @Test
    public void testCreateRequest() throws Exception {
        final URI uri = new URI("http://www.google.com");
        final Header[] responseHeaders = new Header[] {};
        
        expect(client.execute(isA(HttpUriRequest.class)))
            .andReturn(response);
        expect(response.getAllHeaders())
            .andReturn(responseHeaders);
        
        replayMocks();
        
        final ClientHttpRequest request = 
                factory.createRequest(uri, HttpMethod.GET);
        
        final ClientHttpResponse actualResponse = request.execute();
        
        verify(client, response);
    }

}
