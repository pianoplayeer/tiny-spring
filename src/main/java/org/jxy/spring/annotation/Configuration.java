package org.jxy.spring.annotation;

import java.lang.annotation.*;

/**
 * @date 2024/10/15
 * @package org.jxy.spring.ioc.annotation
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Configuration {
	
	/**
	 * Bean name. Default to simple class name with first-letter-lower-case.
	 */
	String value() default "";
	
}


