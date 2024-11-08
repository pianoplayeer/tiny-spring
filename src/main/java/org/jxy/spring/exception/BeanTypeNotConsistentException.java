package org.jxy.spring.exception;

/**
 * @date 2024/10/20
 * @package org.jxy.spring.ioc.exception
 */
public class BeanTypeNotConsistentException extends RuntimeException {
	public BeanTypeNotConsistentException(String msg) {
		super(msg);
	}
}
