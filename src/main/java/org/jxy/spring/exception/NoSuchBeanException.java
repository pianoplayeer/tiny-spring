package org.jxy.spring.exception;

/**
 * @date 2024/10/26
 * @package org.jxy.spring.ioc.exception
 */
public class NoSuchBeanException extends RuntimeException {
	public NoSuchBeanException(String msg) {
		super(msg);
	}
}
