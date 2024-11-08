package org.jxy.spring.exception;

public class DataAccessException extends RuntimeException {
    public DataAccessException(Exception e) {
        super(e);
    }

    public DataAccessException(String msg) {
        super(msg);
    }
}
