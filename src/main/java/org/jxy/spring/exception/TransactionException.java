package org.jxy.spring.exception;

public class TransactionException extends RuntimeException {
    public TransactionException(String msg) {
        super(msg);
    }

    public TransactionException(Exception e) {
        super(e);
    }
}
