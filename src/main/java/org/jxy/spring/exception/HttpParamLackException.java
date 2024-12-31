package org.jxy.spring.exception;

public class HttpParamLackException extends RuntimeException {
    public HttpParamLackException(String message) {
        super(message);
    }
}
