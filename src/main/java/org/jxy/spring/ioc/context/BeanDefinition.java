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

	private String initMethodName;

	private String destroyMethodName;

	private boolean lazy;

	@Setter
	private Method initMethod;

	@Setter
	private Method destroyMethod;

	// 用于@Bean类型的
	// initMethod和destroyMethod为null，应在进行初始化时，到实例类里查找具体方法
	public BeanDefinition(String beanName, Class<?> clazz,
						  int order, boolean primary, boolean lazy,
						  Method factoryMethod,
						  String initMethodName, String destroyMethodName) {
		this.beanName = beanName;
		this.clazz = clazz;
		this.instance = null;
		this.constructor = null;
		this.order = order;
		this.primary = primary;
		this.lazy = lazy;
		this.factoryMethod = factoryMethod;
		this.initMethodName = initMethodName;
		this.destroyMethodName = destroyMethodName;

		factoryMethod.setAccessible(true);
	}

	// 用于@Component类型的Bean
	public BeanDefinition(String beanName, Class<?> clazz,
						  Constructor<?> constructor,
						  int order, boolean primary, boolean lazy,
						  Method initMethod, Method destroyMethod) {
		this.beanName = beanName;
		this.clazz = clazz;
		this.instance = null;
		this.constructor = constructor;
		this.order = order;
		this.primary = primary;
		this.lazy = lazy;
		this.factoryMethod = null;

		this.initMethod = initMethod;
		this.destroyMethod = destroyMethod;

		constructor.setAccessible(true);
		if (initMethod != null) {
			initMethodName = initMethod.getName();
			initMethod.setAccessible(true);
		}
		if (destroyMethod != null) {
			destroyMethodName = destroyMethod.getName();
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
