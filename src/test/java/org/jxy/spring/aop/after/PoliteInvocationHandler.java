package org.jxy.spring.aop.after;

import org.jxy.spring.aop.processor.InvocationHandlerAdapter;
import org.jxy.spring.annotation.Component;

import java.lang.reflect.Method;

@Component
public class PoliteInvocationHandler extends InvocationHandlerAdapter {

    @Override
    public Object after(Object proxy, Object returnValue, Method method, Object[] args) {
        if (returnValue instanceof String s) {
            if (s.endsWith(".")) {
                return s.substring(0, s.length() - 1) + "!";
            }
        }
        return returnValue;
    }
}
