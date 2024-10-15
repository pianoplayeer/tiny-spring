package org.jxy.spring.ioc.annotation;

import java.lang.annotation.*;

/**
 * @date 2024/10/15
 * @package org.jxy.spring.ioc.annotation
 */
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Autowired {
	
	/**
	 * Is required.
	 */
	boolean value() default true;
	
	/**
	 * Bean name if set.
	 */
	String name() default "";
}
