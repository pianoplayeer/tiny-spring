package org.jxy.spring.aop.processor;

import org.jxy.spring.aop.ProxyResolver;
import org.jxy.spring.exception.AopProxyException;
import org.jxy.spring.ioc.context.BeanDefinition;
import org.jxy.spring.ioc.context.BeanPostProcessor;
import org.jxy.spring.ioc.context.ConfigurableApplicationContext;
import org.jxy.spring.utils.ApplicationContextUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public abstract class AutoProxyCreator<T extends Annotation> implements BeanPostProcessor {
    private Map<String, Object> earlyBeanReferences = new HashMap<>();

    public Object getEarlyBeanReference(Object bean, String beanName) {
        earlyBeanReferences.put(beanName, bean);
        return wrapIfNecessary(bean);
    }

    public AutoProxyCreator() {
        ApplicationContextUtils.addAopBeanPostProcessor(this);
    }

    @SuppressWarnings("unchecked")
    private Class<T> getType() {
        Type superClass = getClass().getGenericSuperclass();
        if (superClass instanceof Class) {
            throw new RuntimeException("Missing type parameter.");
        }

        Type type = ((ParameterizedType) superClass).getActualTypeArguments()[0];
        return (Class<T>) type;
    }

    private Object wrapIfNecessary(Object bean) {
        Class<?> clazz = bean.getClass();
        Class<T> type = getType();
        T aspect = clazz.getAnnotation(type);

        if (aspect != null) {
            try {
                String handlerName = (String) aspect.annotationType()
                        .getMethod("value").invoke(aspect);
                return createProxy(bean, handlerName);
            } catch (ReflectiveOperationException e) {
                throw new AopProxyException(String.format("@%s must have value() returned String type.",
                        aspect.annotationType().getSimpleName()), e);
            }
        }

        return bean;
    }

    private Object createProxy(Object bean, String handlerName) {
        ConfigurableApplicationContext ctx = (ConfigurableApplicationContext) ApplicationContextUtils.getApplicationContext();
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
