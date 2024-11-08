package org.jxy.spring.exception;

public class BeanCreationException extends RuntimeException{
    public BeanCreationException(Exception e) {
        super(e);
    }
    
    public BeanCreationException(String msg) {
        super(msg);
    }
}
