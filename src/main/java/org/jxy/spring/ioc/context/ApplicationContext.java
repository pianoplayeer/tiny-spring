package org.jxy.spring.ioc.context;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.jxy.spring.ioc.annotation.*;
import org.jxy.spring.ioc.exception.BeanCreationException;
import org.jxy.spring.ioc.exception.NoUniqueBeanDefinitionException;
import org.jxy.spring.ioc.resolver.PropertyResolver;
import org.jxy.spring.ioc.resolver.ResourceResolver;
import org.jxy.spring.utils.ClassUtil;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @date 2024/10/6
 * @package org.jxy.spring.ioc
 */
@Slf4j
public class ApplicationContext {
	private final Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();

	private final PropertyResolver propertyResolver;
	
	public ApplicationContext(Class<?> configClass, PropertyResolver propertyResolver) {
		this.propertyResolver = propertyResolver;

		Set<String> names = scanForClassNames(configClass);
		createBeanDefinition(names);
	}

	private void createBeanDefinition(Set<String> classNames) {
		for (String name : classNames) {
			Class<?> clazz;
			try {
				clazz = Class.forName(name);
			} catch (ClassNotFoundException e) {
				throw new BeanCreationException(e);
			}

			Component component = ClassUtil.findAnnotation(clazz, Component.class);
			var def = new BeanDefinition(
					ClassUtil.findBeanName(clazz), clazz,
					ClassUtil.findSuitableConstructor(clazz),
					getOrder(clazz), clazz.isAnnotationPresent(Primary.class),
					ClassUtil.findAnnotationMethod(clazz, PostConstruct.class),
					ClassUtil.findAnnotationMethod(clazz, PreDestroy.class)
					);
			addBeanDefinition(def);

			Configuration configuration = ClassUtil.findAnnotation(clazz, Configuration.class);
			if (configuration != null) {
				scanForFactoryBeans(clazz);
			}
		}
	}

	private void scanForFactoryBeans(Class<?> clazz) {
		for (Method method : clazz.getMethods()) {
			if (method.isAnnotationPresent(Bean.class)) {
				Bean bean = method.getAnnotation(Bean.class);
				
				try {
					var def = new BeanDefinition(
							ClassUtil.findBeanName(method), clazz,
							ClassUtil.findSuitableConstructor(clazz),
							getOrder(method), method.isAnnotationPresent(Primary.class),
							method, bean.initMethod(), bean.destroyMethod()
					);
					
					addBeanDefinition(def);
				} catch (NoSuchMethodException e) {
					throw new BeanCreationException(e);
				}
			}
		}
	}

	private void addBeanDefinition(BeanDefinition def) {
		beanDefinitionMap.put(def.getBeanName(), def);
	}

	private int getOrder(Class<?> clazz) {
		Order order = ClassUtil.findAnnotation(clazz, Order.class);
		return order == null ? Integer.MAX_VALUE : order.value();
	}
	
	private int getOrder(Method method) {
		Order order = method.getAnnotation(Order.class);
		return order == null ? Integer.MAX_VALUE : order.value();
	}
	
	private Set<String> scanForClassNames(Class<?> configClass) {
		ComponentScan scan = ClassUtil.findAnnotation(configClass, ComponentScan.class);
		String[] scanPackages = scan == null || scan.value().length == 0 ? new String[]{configClass.getPackageName()} : scan.value();
		Set<String> classNames = new HashSet<>();

		for (String p : scanPackages) {
			ResourceResolver resolver = new ResourceResolver(p);
			var list = resolver.scan(res -> {
				String name = res.name();
				if (name.endsWith(".class")) {
					return name.substring(0, name.length() - 6).replace("/", ".").replace("\\", ".");
				}
				return null;
			});

			classNames.addAll(list);
		}

		Import importConfig = ClassUtil.findAnnotation(configClass, Import.class);
		if (importConfig != null) {
			for (Class<?> clazz : importConfig.value()) {
				classNames.add(clazz.getName());
			}
		}

		return classNames;
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
