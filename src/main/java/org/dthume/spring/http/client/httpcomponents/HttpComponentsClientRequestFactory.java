/**
 * Copyright (C) 2011 David Thomas Hume <dth@dthu.me>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dthume.spring.http.client.httpcomponents;

import static org.apache.http.params.CoreConnectionPNames.SO_TIMEOUT;

import java.net.URI;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpParams;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;

/**
 * Implementation of {@link ClientHttpRequestFactory} based upon Apache
 * HttpComponents/client.
 *
 * Accepts an implementation of {@link HttpClient} as an optional constructor
 * argument, defaulting to a {@link DefaultHttpClient} using a
 * {@link ThreadSafeClientConnManager} if none is supplied.
 * 
 * This class is based upon the class of the same name from Spring 3.1;
 * specifically it uses the same set of defaults for the construction
 * of the default {@code HttpClient} instance.
 *
 * @author dth
 */
public final class HttpComponentsClientRequestFactory
    implements ClientHttpRequestFactory, DisposableBean {

    private static final int DEFAULT_MAX_TOTAL_CONNECTIONS = 100;

    private static final int DEFAULT_MAX_CONNECTIONS_PER_ROUTE = 5;

    private static final int DEFAULT_READ_TIMEOUT_MILLISECONDS = (60 * 1000);
    
    private static final String NULL_CLIENT =
        "HttpClient cannot be null when using single arg constructor";

    private final transient HttpClient client;

    /** Create a new request factory with a {@link DefaultHttpClient}. */
    public HttpComponentsClientRequestFactory() {
        this(createDefaultClient());
    }
    
    private static HttpClient createDefaultClient() {
        final HttpClient client =
                new DefaultHttpClient(createConnectionManager());
        
        final HttpParams params = client.getParams();
        params.setIntParameter(SO_TIMEOUT, DEFAULT_READ_TIMEOUT_MILLISECONDS);

        return client;
    }
    
    private static ClientConnectionManager createConnectionManager() {
        final SchemeRegistry schemes = new SchemeRegistry();
        schemes.register(createHttpScheme());
        schemes.register(createHttpsScheme());
        
        final ThreadSafeClientConnManager connManager =
                new ThreadSafeClientConnManager(schemes);
        connManager.setMaxTotal(DEFAULT_MAX_TOTAL_CONNECTIONS);
        connManager.setDefaultMaxPerRoute(DEFAULT_MAX_CONNECTIONS_PER_ROUTE);
        
        return connManager;
    }
    
    private static Scheme createHttpScheme() {
        return new Scheme("http", 80, PlainSocketFactory.getSocketFactory());
    }
    
    private static Scheme createHttpsScheme() {
        return new Scheme("https", 443, SSLSocketFactory.getSocketFactory());
    }

    /** Create a new request factory with the given {@link HttpClient}. */
    public HttpComponentsClientRequestFactory(final HttpClient client) {
        if (null == client)
            throw new IllegalArgumentException(NULL_CLIENT);
        this.client = client;
    }

    /** {@inheritDoc} */
    public ClientHttpRequest createRequest(final URI uri,
            final HttpMethod method) {
        return new HttpComponentsClientHttpRequest(client, uri, method);
    }

    /** {@inheritDoc} */
    public void destroy() throws Exception {
        client.getConnectionManager().shutdown();
    }
}
