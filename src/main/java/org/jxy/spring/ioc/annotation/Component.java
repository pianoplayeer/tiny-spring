package org.jxy.spring.ioc.annotation;

import java.lang.annotation.*;

/**
 * @date 2024/10/15
 * @package org.jxy.spring.ioc.annotation
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Component {
	
	/**
	 * Bean name. Default to simple class name with first-letter-lowercase.
	 */
	String value() default "";
	
}
