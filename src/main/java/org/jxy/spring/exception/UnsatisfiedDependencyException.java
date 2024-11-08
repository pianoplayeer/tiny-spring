package org.jxy.spring.exception;

public class UnsatisfiedDependencyException extends RuntimeException {
    public UnsatisfiedDependencyException(String msg) {
        super(msg);
    }
}
