package org.jxy.spring.utils;

import org.apache.commons.lang3.AnnotationUtils;
import org.apache.commons.lang3.StringUtils;
import org.jxy.spring.ioc.annotation.Bean;
import org.jxy.spring.ioc.annotation.Component;
import org.jxy.spring.ioc.exception.BeanDefinitionException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClassUtil {
    public static String findBeanName(Class<?> clazz) {
        Component component = ClassUtil.findAnnotation(clazz, Component.class);
        if (StringUtils.isNoneBlank(component.value())) {
            return component.value();
        }

        String simpleName = clazz.getSimpleName();
        return simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1);
    }

    public static String findBeanName(Method method) {
        String name = method.getName();
        Bean bean = method.getAnnotation(Bean.class);

        return StringUtils.isBlank(bean.value()) ? name : bean.value();
    }

    public static <A extends Annotation> A findAnnotation(Class<?> clazz, Class<A> annoClass) {
        A a = clazz.getAnnotation(annoClass);

        List<Annotation> list = Arrays.stream(clazz.getAnnotations()).toList();
        while (!list.isEmpty()) {
            List<Annotation> next = new ArrayList<>();
            for (Annotation annotation : list) {
                if (annoClass.isAssignableFrom(annotation.annotationType())) {
                    if (a != null) {
                        throw new BeanDefinitionException("Duplicate @" + annoClass.getSimpleName() + " found on class " + clazz.getSimpleName());
                    } else {
                        a = (A) annotation;
                    }
                }

                next.addAll(List.of(annotation.annotationType().getAnnotations()));
            }

            list = next;
        }

        return a;
    }

    public static Constructor<?> findSuitableConstructor(Class<?> clazz) {
        Constructor<?>[] constructors = clazz.getConstructors();
        if (constructors.length == 0) {
            constructors = clazz.getDeclaredConstructors();
        }

        if (constructors.length > 1) {
            throw new BeanDefinitionException("More than 1 constructor in bean class " + clazz.getCanonicalName());
        }

        return constructors[0];
    }

    public static <A extends Annotation> Method findAnnotationMethod(Class<?> clazz, Class<A> annoClass) {
        Method[] methods = clazz.getMethods();
        for (Method m : methods) {
            if (m.isAnnotationPresent(annoClass)) {
                return m;
            }
        }

        return null;
    }
}
