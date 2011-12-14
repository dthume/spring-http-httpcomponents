package org.dthume.spring.http.client.httpcomponents;

public class HttpComponentsClientException
    extends RuntimeException {

    public HttpComponentsClientException() {
        super();
    }

    public HttpComponentsClientException(String arg0, Throwable arg1,
            boolean arg2, boolean arg3) {
        super(arg0, arg1, arg2, arg3);
    }

    public HttpComponentsClientException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public HttpComponentsClientException(String arg0) {
        super(arg0);
    }

    public HttpComponentsClientException(Throwable arg0) {
        super(arg0);
    }
}
