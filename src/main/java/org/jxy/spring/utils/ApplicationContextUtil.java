package org.jxy.spring.utils;

import lombok.Getter;
import lombok.Setter;
import org.jxy.spring.ioc.context.ApplicationContext;
import org.jxy.spring.ioc.context.BeanPostProcessor;

import java.util.ArrayList;
import java.util.List;

public class ApplicationContextUtil {
    @Getter
    private static ApplicationContext applicationContext = null;

    @Getter
    private static List<BeanPostProcessor> aopBeanPostProcessors = new ArrayList<>();

    public static void setApplicationContext(ApplicationContext context) {
        aopBeanPostProcessors = new ArrayList<>();
        applicationContext = context;
    }

    public static void addAopBeanPostProcessor(BeanPostProcessor processor) {
        aopBeanPostProcessors.add(processor);
    }
}
