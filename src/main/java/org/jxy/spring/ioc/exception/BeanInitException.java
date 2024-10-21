package org.jxy.spring.ioc.exception;

/**
 * @date 2024/10/21
 * @package org.jxy.spring.ioc.exception
 */
public class BeanInitException extends RuntimeException {
	public BeanInitException(Exception e) {
		super(e);
	}

	public BeanInitException(String msg) {
		super(msg);
	}
}
