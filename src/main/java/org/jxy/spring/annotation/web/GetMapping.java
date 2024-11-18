package org.jxy.spring.annotation.web;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GetMapping {

    /**
     * URL mapping.
     */
    String value();

}
