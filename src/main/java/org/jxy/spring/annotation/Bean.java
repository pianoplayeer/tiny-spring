package org.jxy.spring.annotation;

import java.lang.annotation.*;

/**
 * @date 2024/10/15
 * @package org.jxy.spring.ioc.annotation
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Bean {
	
	/**
	 * Bean name. default to method name.
	 */
	String value() default "";
	
	String initMethod() default "";
	
	String destroyMethod() default "";
}
