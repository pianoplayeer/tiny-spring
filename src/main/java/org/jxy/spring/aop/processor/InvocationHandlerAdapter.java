package org.jxy.spring.aop.processor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public abstract class InvocationHandlerAdapter implements Enhancer, InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        before(proxy, method, args);
        Object obj = method.invoke(proxy, args);
        obj = after(proxy, obj, method, args);

        return obj;
    }
}
