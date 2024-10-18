package org.jxy.spring.ioc.context;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @date 2024/10/6
 * @package org.jxy.spring.ioc.resolver
 */
@Slf4j
@Getter
public class BeanDefinition implements Comparable<BeanDefinition> {
	private String beanName;
	
	private Class<?> clazz;

	@Setter
	private Object instance;
	
	private Constructor<?> constructor;
	
	private int order;
	
	private boolean primary;
	
	private Method factoryMethod;
	
	private Method initMethod;
	
	private Method destroyMethod;

	public BeanDefinition(String beanName, Class<?> clazz,
						  int order, boolean primary, Method factoryMethod,
						  String initMethod, String destroyMethod) {
		this.beanName = beanName;
		this.clazz = clazz;
		this.instance = null;
		this.constructor = null;
		this.order = order;
		this.primary = primary;
		this.factoryMethod = factoryMethod;

		try {
			if (StringUtils.isNoneEmpty(initMethod)) {
				this.initMethod = clazz.getMethod(initMethod);
			}
			if (StringUtils.isNoneEmpty(destroyMethod)) {
				this.destroyMethod = clazz.getMethod(destroyMethod);
			}
		} catch (NoSuchMethodException e) {
			log.warn("No such init/destroy method for bean {}", beanName);
		}

		factoryMethod.setAccessible(true);
		if (this.initMethod != null) {
			this.initMethod.setAccessible(true);
		}
		if (this.destroyMethod != null) {
			this.destroyMethod.setAccessible(true);
		}
	}

	public BeanDefinition(String beanName, Class<?> clazz,
						  Constructor<?> constructor,
						  int order, boolean primary,
						  Method initMethod, Method destroyMethod) {
		this.beanName = beanName;
		this.clazz = clazz;
		this.instance = null;
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

	public String getCreateDetail() {
		if (this.factoryMethod != null) {
			String params = String.join(", ", Arrays.stream(this.factoryMethod.getParameterTypes()).map(Class::getSimpleName).toArray(String[]::new));
			return this.factoryMethod.getDeclaringClass().getSimpleName() + "." + this.factoryMethod.getName() + "(" + params + ")";
		}
		return null;
	}

	@Override
	public String toString() {
		return "BeanDefinition [name=" + beanName + ", beanClass=" + clazz.getName() + ", factory=" + getCreateDetail() + ", init-method="
				+ (initMethod == null ? "null" : initMethod.getName()) + ", destroy-method=" + (destroyMethod == null ? "null" : destroyMethod.getName())
				+ ", primary=" + primary + ", instance=" + instance + "]";
	}
}
