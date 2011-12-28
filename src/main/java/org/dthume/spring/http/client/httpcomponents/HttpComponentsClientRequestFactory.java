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

import java.net.URI;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;

/**
 * Implementation of {@link ClientHttpRequestFactory} based upon Apache
 * HttpComponents/client.
 *
 * Accepts an implementation of {@link HttpClient} as an optional constructor
 * argument, defaulting to {@link DefaultHttpClient} if none is supplied.
 *
 * @author dth
 */
public final class HttpComponentsClientRequestFactory
    implements ClientHttpRequestFactory {

    private static final String NULL_CLIENT =
        "HttpClient cannot be null when using single arg constructor";

    private final HttpClient client;

    /** Create a new request factory with a {@link DefaultHttpClient}. */
    public HttpComponentsClientRequestFactory() {
        this(new DefaultHttpClient());
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
}
