package org.jxy.spring.ioc.context;

import lombok.Getter;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * @date 2024/10/6
 * @package org.jxy.spring.ioc.resolver
 */
@Getter
public class BeanDefinition implements Comparable<BeanDefinition> {
	private String beanName;
	
	private Class<?> clazz;
	
	private Object instance;
	
	private Constructor<?> constructor;
	
	private int order;
	
	private boolean primary;
	
	private Method factoryMethod;
	
	private Method initMethod;
	
	private Method destroyMethod;
	
	@Override
	public int compareTo(BeanDefinition o) {
		int cmp = Integer.compare(order, o.order);
		if (cmp != 0) {
			return cmp;
		}
		
		return beanName.compareTo(o.beanName);
	}
}
