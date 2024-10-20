package org.jxy.spring.ioc.exception;

/**
 * @date 2024/10/20
 * @package org.jxy.spring.ioc.exception
 */
public class BeanInjectionException extends RuntimeException {
	public BeanInjectionException(String msg) {
		super(msg);
	}
	
	public BeanInjectionException(Exception e) {
		super(e);
	}
}
