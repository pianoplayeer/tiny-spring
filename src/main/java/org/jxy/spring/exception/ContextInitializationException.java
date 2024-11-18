package org.jxy.spring.exception;

public class ContextInitializationException extends RuntimeException {
    public ContextInitializationException(String msg) {
        super(msg);
    }

    public ContextInitializationException(Exception e) {
        super(e);
    }
}
