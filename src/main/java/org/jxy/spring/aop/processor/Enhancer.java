package org.jxy.spring.aop.processor;

import java.lang.reflect.Method;

public interface Enhancer {
    default void before(Object proxy, Method method, Object[] args) {
    }

    default Object after(Object proxy, Object returnValue, Method method, Object[] args) {
        return returnValue;
    }
}
