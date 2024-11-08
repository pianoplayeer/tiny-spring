package org.jxy.spring.exception;

public class BeanDefinitionException extends RuntimeException {
    public BeanDefinitionException(String msg) {
        super(msg);
    }

    public BeanDefinitionException(String msg, Exception e) {
        super(msg, e);
    }
}
