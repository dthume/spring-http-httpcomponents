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

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;

/**
 * Implementation of {@link ClientHttpResponse} based upon Apache
 * HttpComponents/client.
 *
 * @author dth
 */
public final class HttpComponentsClientHttpResponse
    implements ClientHttpResponse {

    private final HttpResponse response;
    private final HttpHeaders responseHeaders;

    /** Create a client response based on the given {@link HttpResponse}. */
    public HttpComponentsClientHttpResponse(final HttpResponse response) {
        this.response = response;
        this.responseHeaders = toHttpHeaders(response);
    }

    private static HttpHeaders toHttpHeaders(final HttpResponse response) {
        final HttpHeaders headers = new HttpHeaders();
        for (final Header header : response.getAllHeaders())
            headers.add(header.getName(), header.getValue());
        return headers;
    }

    /** {@inheritDoc} */
    public InputStream getBody() throws IOException {
        return response.getEntity().getContent();
    }

    /** {@inheritDoc} */
    public HttpHeaders getHeaders() {
        return responseHeaders;
    }

    /** {@inheritDoc} */
    public HttpStatus getStatusCode() throws IOException {
        final int status = response.getStatusLine().getStatusCode();
        return HttpStatus.valueOf(status);
    }

    /** {@inheritDoc} */
    public String getStatusText() throws IOException {
        return response.getStatusLine().getReasonPhrase();
    }

    /** {@inheritDoc} */
    public void close() {
        try {
            EntityUtils.consume(response.getEntity());
        } catch (IOException e) {
            throw new HttpComponentsClientException(
                    "Caught exception consuming response content", e);
        }
    }
}
