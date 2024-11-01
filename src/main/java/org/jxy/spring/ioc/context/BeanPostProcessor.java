package org.jxy.spring.ioc.context;

public interface BeanPostProcessor {

    /**
     * Invoked before bean.init() called.
     */
    default Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    /**
     * Invoked after bean.init() called.
     */
    default Object postProcessAfterInitialization(Object bean, String beanName) {
        return bean;
    }
}

