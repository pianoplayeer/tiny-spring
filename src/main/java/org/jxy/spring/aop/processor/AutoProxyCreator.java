package org.jxy.spring.aop.processor;

import org.jxy.spring.aop.ProxyResolver;
import org.jxy.spring.aop.annotation.Aspect;
import org.jxy.spring.aop.exception.AopProxyException;
import org.jxy.spring.ioc.context.BeanDefinition;
import org.jxy.spring.ioc.context.BeanPostProcessor;
import org.jxy.spring.ioc.context.ConfigurableApplicationContext;
import org.jxy.spring.utils.ApplicationContextUtil;

import java.lang.reflect.InvocationHandler;
import java.util.HashMap;
import java.util.Map;

public class AutoProxyCreator implements BeanPostProcessor {
    private Map<String, Object> earlyBeanReferences = new HashMap<>();

    public Object getEarlyBeanReference(Object bean, String beanName) {
        earlyBeanReferences.put(beanName, bean);
        return wrapIfNecessary(bean);
    }

    private Object wrapIfNecessary(Object bean) {
        Class<?> clazz = bean.getClass();
        Aspect aspect = clazz.getAnnotation(Aspect.class);

        if (aspect != null) {
            String handlerName = aspect.value();
            return createProxy(bean, handlerName);
        }
        return bean;
    }

    private Object createProxy(Object bean, String handlerName) {
        ConfigurableApplicationContext ctx = (ConfigurableApplicationContext) ApplicationContextUtil.getApplicationContext();
        Object handler = ctx.getBean(handlerName);

        if (handler == null) {
            BeanDefinition def = ctx.findBeanDefinition(handlerName);
            if (def == null) {
                throw new AopProxyException(String.format("No such handler named %s.", handlerName));
            }
            handler = ctx.createBean(def);
        }

        if (handler instanceof InvocationHandler) {
            return ProxyResolver.createProxy(bean, (InvocationHandler) handler);
        } else {
            throw new AopProxyException("The type of handler %s is not InvocationHandler. " +
                    "You should implement InvocationHandler or InvocationHandlerAdapter to enhance the aspect.");
        }
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if (earlyBeanReferences.remove(beanName) != bean) {
            return wrapIfNecessary(bean);
        }
        return bean;
    }
}
