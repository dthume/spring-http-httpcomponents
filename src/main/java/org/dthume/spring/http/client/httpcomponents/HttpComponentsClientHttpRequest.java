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

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.AbstractClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;

/**
 * Implementation of
 * {@link org.springframework.http.client.ClientHttpRequest}
 * based upon Apache HttpComponents/client.
 *
 * @author dth
 */
public final class HttpComponentsClientHttpRequest
    extends AbstractClientHttpRequest {

    private final HttpClient client;
    private final URI uri;
    private final HttpMethod method;

    public HttpComponentsClientHttpRequest(
            final HttpClient client,
            final URI uri,
            final HttpMethod method) {
        this.client = client;
        this.uri = uri;
        this.method = method;
    }

    /** {@inheritDoc} */
    public URI getURI() { return uri; }

    /** {@inheritDoc} */
    public HttpMethod getMethod() { return method; }

    @Override
    protected ClientHttpResponse executeInternal(
            final HttpHeaders headers,
            final byte[] bufferedOutput) throws IOException {
        final HttpUriRequest request =
                createAndPrepareRequest(headers, bufferedOutput);

        final HttpResponse response = client.execute(request);

        return new HttpComponentsClientHttpResponse(response);
    }

    private HttpUriRequest createAndPrepareRequest(
            final HttpHeaders headers,
            final byte[] bufferedOutput) {
        final HttpUriRequest request = createRequest();
        addHeaders(request, headers);
        addBody(request, bufferedOutput);
        return request;
    }

    private HttpUriRequest createRequest() {
        final URI requestUri = getURI();
        final HttpMethod requestMethod = getMethod();

        switch (requestMethod) {
        case DELETE:
            return new HttpDelete(requestUri);
        case GET:
            return new HttpGet(requestUri);
        case HEAD:
            return new HttpHead(requestUri);
        case PUT:
            return new HttpPut(requestUri);
        case POST:
            return new HttpPost(requestUri);
        case OPTIONS:
            return new HttpOptions(requestUri);
        default:
            final String msg = "Unknown Http Method: " + requestMethod;
            throw new IllegalArgumentException(msg);
        }
    }

    private void addHeaders(final HttpUriRequest request,
            final HttpHeaders headers) {
        for (Map.Entry<String, List<String>> entry : headers.entrySet())
            for (final String value : entry.getValue())
                request.addHeader(entry.getKey(), value);
    }

    private void addBody(final HttpRequest request,
            final byte[] output) {
        if (request instanceof HttpEntityEnclosingRequest) {
            final HttpEntity entity = new ByteArrayEntity(output);
            ((HttpEntityEnclosingRequest) request).setEntity(entity);
        }
    }
}
