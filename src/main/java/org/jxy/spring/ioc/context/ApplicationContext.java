package org.jxy.spring.ioc.context;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.jxy.spring.ioc.annotation.*;
import org.jxy.spring.ioc.exception.BeanCreationException;
import org.jxy.spring.ioc.exception.BeanDefinitionException;
import org.jxy.spring.ioc.exception.NoUniqueBeanDefinitionException;
import org.jxy.spring.ioc.exception.UnsatisfiedDependencyException;
import org.jxy.spring.ioc.resolver.PropertyResolver;
import org.jxy.spring.ioc.resolver.ResourceResolver;
import org.jxy.spring.utils.ClassUtil;

import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @date 2024/10/6
 * @package org.jxy.spring.ioc
 */
@Slf4j
public class ApplicationContext {

	private final PropertyResolver propertyResolver;

	private final Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();

	private final Set<String> creatingBeanNames = new HashSet<>();
	
	public ApplicationContext(Class<?> configClass, PropertyResolver propertyResolver) {
		this.propertyResolver = propertyResolver;

		Set<String> names = scanForClassNames(configClass);
		createBeanDefinition(names);

		beanDefinitionMap.values().stream()
				.filter(def -> def.getClazz().isAnnotationPresent(Configuration.class))
				.forEach(this::createBeanAsEarlySingleton);

		beanDefinitionMap.values().stream()
				.filter(def -> def.getInstance() == null)
				.sorted()
				.forEach(def -> {
					if (def.getInstance() == null) {
						createBeanAsEarlySingleton(def);
					}
				});
	}

	private void createBeanAsEarlySingleton(BeanDefinition beanDefinition) {
		if (!creatingBeanNames.add(beanDefinition.getBeanName())) {
			throw new UnsatisfiedDependencyException(String.format("Cannot create bean %s since it is being created.", beanDefinition.getBeanName()));
		}

		Executable createFunc = beanDefinition.getFactoryMethod() == null ?
				beanDefinition.getConstructor() : beanDefinition.getFactoryMethod();

		Parameter[] params = createFunc.getParameters();
		Object[] args = new Object[params.length];

		for (int i = 0; i < params.length; i++) {
			Value value = params[i].getAnnotation(Value.class);
			Autowired autowired = params[i].getAnnotation(Autowired.class);


		}

		beanDefinition.setInstance();
	}

	private void createBeanDefinition(Set<String> classNames) {
		for (String name : classNames) {
			Class<?> clazz;
			try {
				clazz = Class.forName(name);
			} catch (ClassNotFoundException e) {
				throw new BeanCreationException(e);
			}

			if (clazz.isAnnotation()) {
				continue;
			}

			if (ClassUtil.findAnnotation(clazz, Component.class) != null) {
				var def = new BeanDefinition(
						ClassUtil.findBeanName(clazz), clazz,
						ClassUtil.findSuitableConstructor(clazz),
						getOrder(clazz), clazz.isAnnotationPresent(Primary.class),
						ClassUtil.findAnnotationMethod(clazz, PostConstruct.class),
						ClassUtil.findAnnotationMethod(clazz, PreDestroy.class)
				);
				addBeanDefinition(def);
			}

			Configuration configuration = ClassUtil.findAnnotation(clazz, Configuration.class);
			if (configuration != null) {
				scanForFactoryBeans(clazz);
			}
		}
	}

	private void scanForFactoryBeans(Class<?> clazz) {
		for (Method method : clazz.getDeclaredMethods()) {
			if (method.isAnnotationPresent(Bean.class)) {
				Bean bean = method.getAnnotation(Bean.class);
				int mod = method.getModifiers();

				if (Modifier.isAbstract(mod)) {
					throw new BeanDefinitionException("@Bean method " + clazz.getName() + "." + method.getName() + " must not be abstract.");
				}
				if (Modifier.isFinal(mod)) {
					throw new BeanDefinitionException("@Bean method " + clazz.getName() + "." + method.getName() + " must not be final.");
				}
				if (Modifier.isPrivate(mod)) {
					throw new BeanDefinitionException("@Bean method " + clazz.getName() + "." + method.getName() + " must not be private.");
				}

				Class<?> beanClass = method.getReturnType();
				if (beanClass.isPrimitive()) {
					throw new BeanDefinitionException("@Bean method " + clazz.getName() + "." + method.getName() + " must not return primitive type.");
				}
				if (beanClass == void.class || beanClass == Void.class) {
					throw new BeanDefinitionException("@Bean method " + clazz.getName() + "." + method.getName() + " must not return void.");
				}

				var def = new BeanDefinition(
						ClassUtil.findBeanName(method), beanClass,
						getOrder(method), method.isAnnotationPresent(Primary.class),
						method, bean.initMethod(), bean.destroyMethod()
				);

				addBeanDefinition(def);
				log.info("define bean: {}", def);
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
	
	public List<BeanDefinition> findBeanDefinitions(Class<?> type) {
		return beanDefinitionMap.values().stream()
					   .filter(e -> type.isAssignableFrom(e.getClazz()))
					   .sorted()
					   .collect(Collectors.toList());
	}
}
