package org.jxy.spring.ioc.annotation;

import java.lang.annotation.*;

/**
 * @date 2024/10/15
 * @package org.jxy.spring.ioc.annotation
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Primary {
}
