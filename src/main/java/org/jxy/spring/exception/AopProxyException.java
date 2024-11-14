package org.jxy.spring.exception;

public class AopProxyException extends RuntimeException {
    public AopProxyException(String msg) {
        super(msg);
    }

    public AopProxyException(String msg, Exception e) {
        super(msg, e);
    }
}
