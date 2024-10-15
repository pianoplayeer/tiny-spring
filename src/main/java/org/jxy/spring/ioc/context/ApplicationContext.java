package org.jxy.spring.ioc.context;

import lombok.extern.slf4j.Slf4j;
import org.jxy.spring.ioc.annotation.ComponentScan;
import org.jxy.spring.ioc.exception.NoUniqueBeanDefinitionException;
import org.jxy.spring.ioc.resolver.PropertyResolver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @date 2024/10/6
 * @package org.jxy.spring.ioc
 */
@Slf4j
public class ApplicationContext {
	private Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();
	
	public ApplicationContext(Class<?> configClass, PropertyResolver propertyResolver) {
	
	}
	
	private Set<String> scanForClassNames(Class<?> configClass) {
		ComponentScan scan = configClass.getAnnotation(ComponentScan.class);
		
		
	}
	
	public BeanDefinition findBeanDefinition(String name) {
		return beanDefinitionMap.get(name);
	}
	
	public BeanDefinition findBeanDefinition(Class<?> type) {
		var definitions = findBeanDefinitions(type);
		if (definitions.isEmpty()) {
			return null;
		}
		
		if (definitions.size() == 1) {
			return definitions.get(0);
		}
		
		var primaryList = definitions.stream()
				.filter(BeanDefinition::isPrimary).toList();
		if (primaryList.size() == 1) {
			return primaryList.get(0);
		} else if (primaryList.isEmpty()) {
			throw new NoUniqueBeanDefinitionException(String.format("Multiple bean with type '%s' found, but no @Primary specified.", type.getName()));
		} else {
			throw new NoUniqueBeanDefinitionException(String.format("Multiple bean with type '%s' found, and multiple @Primary specified.", type.getName()));
		}
	}
	
	private List<BeanDefinition> findBeanDefinitions(Class<?> type) {
		return beanDefinitionMap.values().stream()
					   .filter(e -> type.isAssignableFrom(e.getClazz()))
					   .sorted()
					   .collect(Collectors.toList());
	}
}
