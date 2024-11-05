package org.jxy.spring.aop.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Aspect {
    /**
     * 切面bean名称
     */
    String value();
}
