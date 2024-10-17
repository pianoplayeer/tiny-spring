package org.jxy.spring.ioc.exception;

public class BeanCreationException extends RuntimeException{
    public BeanCreationException(Exception e) {
        super(e);
    }
}
