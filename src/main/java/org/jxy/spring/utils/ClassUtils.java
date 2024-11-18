package org.jxy.spring.utils;

import org.apache.commons.lang3.StringUtils;
import org.jxy.spring.annotation.Bean;
import org.jxy.spring.annotation.Component;
import org.jxy.spring.exception.BeanDefinitionException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class ClassUtils {
    public static String findBeanName(Class<?> clazz) {
        Component component = clazz.getAnnotation(Component.class);

        if (component != null && StringUtils.isNoneBlank(component.value())) {
            return component.value();
        }

        if (component == null) {
            for (Annotation annotation : clazz.getAnnotations()) {
                if (findAnnotation(annotation.annotationType(), Component.class) != null) {
                    try {
                        String name = (String) annotation.annotationType().getMethod("value").invoke(annotation);
                        if (StringUtils.isNoneBlank(name)) {
                            return name;
                        }
                        break;
                    } catch (Exception e) {
                        throw new BeanDefinitionException("Cannot get annotation value.", e);
                    }
                }
            }
        }

        String simpleName = clazz.getSimpleName();
        return Character.toLowerCase(simpleName.charAt(0)) + simpleName.substring(1);
    }

    public static String findBeanName(Method method) {
        String name = method.getName();
        Bean bean = method.getAnnotation(Bean.class);

        return StringUtils.isBlank(bean.value()) ? name : bean.value();
    }

    public static <A extends Annotation> A findAnnotation(Class<?> clazz, Class<A> annoClass) {
        A a = clazz.getAnnotation(annoClass);

        for (Annotation annotation : clazz.getAnnotations()) {
            Class<? extends Annotation> annoType = annotation.annotationType();
            if (!annoType.getPackageName().equals("java.lang.annotation")) {
                A found = findAnnotation(annoType, annoClass);
                if (found != null) {
                   if (a != null) {
                       throw new BeanDefinitionException("Duplicate @" + annoClass.getSimpleName() + " found on class " + clazz.getSimpleName());
                   }
                   a = found;
                }
            }
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
        Method[] methods = clazz.getDeclaredMethods();
        for (Method m : methods) {
            if (m.isAnnotationPresent(annoClass)) {
                return m;
            }
        }

        return null;
    }
}
