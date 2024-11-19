package org.jxy.spring.utils;

import lombok.Getter;
import org.jxy.spring.ioc.context.ApplicationContext;
import org.jxy.spring.ioc.context.BeanPostProcessor;
import org.jxy.spring.ioc.context.ConfigurableApplicationContext;

import java.util.ArrayList;
import java.util.List;

public class ApplicationContextUtils {
    @Getter
    private static ApplicationContext applicationContext = null;

    @Getter
    private static List<BeanPostProcessor> aopBeanPostProcessors = new ArrayList<>();

    public static void initApplicationContext(ApplicationContext context) {
        aopBeanPostProcessors = new ArrayList<>();
        applicationContext = context;
    }

    public static void addAopBeanPostProcessor(BeanPostProcessor processor) {
        aopBeanPostProcessors.add(processor);
    }

    public static ConfigurableApplicationContext createApplicationContext() {
        return
    }
}
