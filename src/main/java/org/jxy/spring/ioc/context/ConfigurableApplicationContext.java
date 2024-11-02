package org.jxy.spring.ioc.context;

import jakarta.annotation.Nullable;

import java.util.List;

/**
 * @date 2024/11/2
 * @package org.jxy.spring.ioc.context
 */
public interface ConfigurableApplicationContext extends ApplicationContext {
	
	List<BeanDefinition> findBeanDefinitions(Class<?> type);
	
	@Nullable
	BeanDefinition findBeanDefinition(Class<?> type);
	
	@Nullable
	BeanDefinition findBeanDefinition(String name);
	
	@Nullable
	BeanDefinition findBeanDefinition(String name, Class<?> requiredType);
	
	Object createBean(BeanDefinition def);
}
