package org.jxy.spring.utils;

import lombok.Getter;
import lombok.Setter;
import org.jxy.spring.ioc.context.ApplicationContext;
import org.jxy.spring.ioc.context.BeanPostProcessor;

public class ApplicationContextUtil {
    @Getter
    @Setter
    private static ApplicationContext applicationContext = null;

    @Setter
    @Getter
    private static BeanPostProcessor aopBeanPostProcessor = null;
}
