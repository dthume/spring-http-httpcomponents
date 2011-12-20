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
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
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
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.AbstractClientHttpRequest;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;

/**
 * Implementation of {@link ClientHttpRequestFactory} based upon Apache
 * HttpComponents/client.
 *
 * @see http://hc.apache.org/httpcomponents-client-ga/index.html
 *
 * @author dth
 */
public final class HttpComponentsClientRequestFactory
    implements ClientHttpRequestFactory {

    private final HttpClient client;

    public HttpComponentsClientRequestFactory() {
        this(new DefaultHttpClient());
    }

    public HttpComponentsClientRequestFactory(final HttpClient client) {
        this.client = client;
    }

    public ClientHttpRequest createRequest(final URI uri,
            final HttpMethod method) {
        return new ClientRequest(uri, method);
    }

    private class ClientRequest extends AbstractClientHttpRequest {

        private final URI uri;
        private final HttpMethod method;

        ClientRequest(final URI uri, final HttpMethod method) {
            this.uri = uri;
            this.method = method;
        }

        public URI getURI() {
            return uri;
        }

        public HttpMethod getMethod() {
            return method;
        }

        @Override
        protected ClientHttpResponse executeInternal(
                final HttpHeaders headers,
                final byte[] bufferedOutput) throws IOException {

            final HttpUriRequest request = createRequest();
            addHeaders(request, headers);
            addBody(request, bufferedOutput);

            final HttpResponse response = client.execute(request);
            final HttpHeaders responseHeaders = toHttpHeaders(response);

            return new ClientHttpResponse() {
                public InputStream getBody() throws IOException {
                    return response.getEntity().getContent();
                }

                public HttpHeaders getHeaders() {
                    return responseHeaders;
                }

                public HttpStatus getStatusCode() throws IOException {
                    final int status = response.getStatusLine().getStatusCode();
                    return HttpStatus.valueOf(status);
                }

                public String getStatusText() throws IOException {
                    return response.getStatusLine().getReasonPhrase();
                }

                public void close() {
                    try {
                        EntityUtils.consume(response.getEntity());
                    } catch (IOException e) {
                        throw new HttpComponentsClientException(
                                "Caught exception consuming repsonse content",
                                e);
                    }
                }
            };
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

        private HttpHeaders toHttpHeaders(final HttpResponse response) {
            final HttpHeaders headers = new HttpHeaders();
            for (final Header header : response.getAllHeaders())
                headers.add(header.getName(), header.getValue());
            return headers;
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

}
