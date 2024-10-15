package org.jxy.spring.ioc.annotation;

import java.lang.annotation.*;

/**
 * @date 2024/10/15
 * @package org.jxy.spring.ioc.annotation
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface Import {
	Class<?>[] value();
}
