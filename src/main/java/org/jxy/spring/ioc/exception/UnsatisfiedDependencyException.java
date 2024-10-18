package org.jxy.spring.ioc.exception;

public class UnsatisfiedDependencyException extends RuntimeException {
    public UnsatisfiedDependencyException(String msg) {
        super(msg);
    }
}
