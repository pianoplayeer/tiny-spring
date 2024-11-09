package org.jxy.spring.jdbc.transaction;

import org.jxy.spring.ioc.context.BeanPostProcessor;

/**
 * @date 2024/11/9
 * @package org.jxy.spring.jdbc.transaction
 */
public class TransactionBeanPostProcessor implements BeanPostProcessor {
	
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) {
		return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
	}
}
