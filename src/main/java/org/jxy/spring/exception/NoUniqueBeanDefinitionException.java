package org.jxy.spring.exception;

/**
 * @date 2024/10/15
 * @package org.jxy.spring.ioc.exception
 */
public class NoUniqueBeanDefinitionException extends RuntimeException {
	public NoUniqueBeanDefinitionException(String msg) {
		super(msg);
	}
}
