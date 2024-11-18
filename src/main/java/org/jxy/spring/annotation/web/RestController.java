package org.jxy.spring.annotation.web;

import org.jxy.spring.annotation.Component;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface RestController {
    String value() default "";
}
