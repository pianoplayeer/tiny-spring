package org.jxy.spring.ioc.context;

import java.util.List;

/**
 * @date 2024/11/2
 * @package org.jxy.spring.ioc.context
 */
public interface ApplicationContext extends AutoCloseable {
	// 是否存在指定name的Bean？
	boolean containsBean(String name);
	
	// 根据name返回唯一Bean，未找到抛出NoSuchBeanDefinitionException
	<T> T getBean(String name);
	
	// 根据name返回唯一Bean，未找到抛出NoSuchBeanDefinitionException
	<T> T getBean(String name, Class<T> requiredType);
	
	// 根据type返回唯一Bean，未找到抛出NoSuchBeanDefinitionException
	<T> T getBean(Class<T> requiredType);
	
	// 根据type返回一组Bean，未找到返回空List
	<T> List<T> getBeans(Class<T> requiredType);
	
	// 关闭并执行所有bean的destroy方法
	void close();
}
