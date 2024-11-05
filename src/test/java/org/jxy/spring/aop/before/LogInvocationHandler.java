package org.jxy.spring.aop.before;

import org.jxy.spring.aop.processor.InvocationHandlerAdapter;
import org.jxy.spring.ioc.annotation.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

@Component
public class LogInvocationHandler extends InvocationHandlerAdapter {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void before(Object proxy, Method method, Object[] args) {
        logger.info("[Before] {}()", method.getName());
    }
}
