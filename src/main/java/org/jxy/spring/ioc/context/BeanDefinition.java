package org.jxy.spring.ioc.context;

import lombok.AllArgsConstructor;
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

	public BeanDefinition(String beanName, Class<?> clazz,
						  Object instance, Constructor<?> constructor,
						  int order, boolean primary, Method factoryMethod,
						  String initMethod, String destroyMethod) throws NoSuchMethodException {
		this.beanName = beanName;
		this.clazz = clazz;
		this.instance = instance;
		this.constructor = constructor;
		this.order = order;
		this.primary = primary;
		this.factoryMethod = factoryMethod;

		this.initMethod = clazz.getMethod(initMethod);
		this.destroyMethod = clazz.getMethod(destroyMethod);

		constructor.setAccessible(true);
		this.initMethod.setAccessible(true);
		this.destroyMethod.setAccessible(true);
	}

	public BeanDefinition(String beanName, Class<?> clazz,
						  Object instance, Constructor<?> constructor,
						  int order, boolean primary,
						  Method initMethod, Method destroyMethod) {
		this.beanName = beanName;
		this.clazz = clazz;
		this.instance = instance;
		this.constructor = constructor;
		this.order = order;
		this.primary = primary;
		this.factoryMethod = null;

		this.initMethod = initMethod;
		this.destroyMethod = destroyMethod;

		constructor.setAccessible(true);
		if (initMethod != null) {
			initMethod.setAccessible(true);
		}
		if (destroyMethod != null) {
			destroyMethod.setAccessible(true);
		}
	}
	
	@Override
	public int compareTo(BeanDefinition o) {
		int cmp = Integer.compare(order, o.order);
		if (cmp != 0) {
			return cmp;
		}
		
		return beanName.compareTo(o.beanName);
	}
}
