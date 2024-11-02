package org.jxy.spring.ioc.scan.cycle;

import org.jxy.spring.ioc.annotation.Component;
import org.jxy.spring.ioc.context.BeanPostProcessor;

/**
 * @date 2024/11/2
 * @package org.jxy.spring.ioc.scan.cycle
 */

public class PostProcessor implements BeanPostProcessor {
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) {
		if (bean.getClass() == A.class) {
			return new A1();
		}
		return bean;
	}
	
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) {
		return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
	}
}
