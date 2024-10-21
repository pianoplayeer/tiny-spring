package org.jxy.spring.ioc.exception;

/**
 * @date 2024/10/21
 * @package org.jxy.spring.ioc.exception
 */
public class BeanPostInitException extends RuntimeException {
	public BeanPostInitException(Exception e) {
		super(e);
	}

	public BeanPostInitException(String msg) {
		super(msg);
	}
}
