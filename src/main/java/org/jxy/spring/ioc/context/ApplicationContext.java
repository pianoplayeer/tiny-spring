package org.jxy.spring.ioc.context;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jxy.spring.ioc.annotation.*;
import org.jxy.spring.ioc.exception.*;
import org.jxy.spring.ioc.resolver.PropertyResolver;
import org.jxy.spring.ioc.resolver.ResourceResolver;
import org.jxy.spring.utils.ClassUtil;

import java.lang.reflect.*;
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

	private final Set<String> registeredBeanNames = new HashSet<>();

	private final Set<String> creatingBeanNames = new HashSet<>();

	private final List<BeanPostProcessor> beanPostProcessors = new ArrayList<>();
	
	public ApplicationContext(Class<?> configClass, PropertyResolver propertyResolver) {
		this.propertyResolver = propertyResolver;

		Set<String> names = scanForClassNames(configClass);
		createBeanDefinition(names);

		createBeans();
	}

	/**
	 * 实例化 -> 属性注入 -> BeanPostProcessor.postProcessBeforeInit
	 * -> init -> BeanPostProcessor.postProcessAfterInit
	 */
	private void createBeans() {
		// 先生成Configuration工厂Bean
		beanDefinitionMap.values().stream()
				.filter(def -> def.getClazz().isAnnotationPresent(Configuration.class))
				.forEach(this::createBean);

		List<BeanPostProcessor> processors = beanDefinitionMap.values().stream()
				.filter(def -> BeanPostProcessor.class.isAssignableFrom(def.getClazz()))
				.sorted()
				.map(def -> (BeanPostProcessor) createBean(def))
				.toList();
		beanPostProcessors.addAll(processors);

		beanDefinitionMap.values().stream()
				.filter(def -> def.getInstance() == null && !def.isLazy())
				.sorted()
				.forEach(def -> {
					if (def.getInstance() == null) {
						createBean(def);
					}
				});
	}

	private Object createBean(BeanDefinition def) {

	}
	
	private void initiateBeans() {
		beanDefinitionMap.values().stream()
				.filter(def -> StringUtils.isNoneEmpty(def.getInitMethodName()))
				.forEach(def -> {
					try {
						Method initMethod = def.getInitMethod();

						if (initMethod == null) {
							initMethod = def.getInstance().getClass().getDeclaredMethod(def.getInitMethodName());
							def.setInitMethod(initMethod);
						}

						if (initMethod != null) {
							initMethod.setAccessible(true);
							initMethod.invoke(def.getInstance());
						} else {
							throw new BeanInitException(String.format("No init method named %s in Bean %s@%s.", def.getInitMethodName(), def.getBeanName(), def.getClazz().getName()));
						}
					} catch (ReflectiveOperationException e) {
						throw new BeanInitException(e);
					}
				});
	}
	
	private void populateBeans() {
		beanDefinitionMap.values().forEach(def -> {
			try {
				populateBean(def);
			} catch (Exception e) {
				throw new BeanInjectionException(e);
			}
		});
	}
	
	private void populateBean(BeanDefinition def) throws ReflectiveOperationException {
		if (def.getInstance() == null) {
			throw new BeanInjectionException(String.format("Cannot inject bean %s before instantiate it.", def.getBeanName()));
		}
		
		// @Autowired与@Value属性反射注入
		Class<?> clazz = def.getClazz();
		Object instance = def.getInstance();
		String beanName = def.getBeanName();
		
		doPopulateBean(beanName, instance, clazz);
		
		Class<?> superClazz = clazz.getSuperclass();
		if (superClazz != null) {
			doPopulateBean(beanName, instance, superClazz);
		}
	}
	
	private void doPopulateBean(String beanName, Object instance, Class<?> clazz)
			throws ReflectiveOperationException {
		Field[] fields = clazz.getDeclaredFields();
		
		for (Field field : fields) {
			Autowired autowired = field.getAnnotation(Autowired.class);
			Value value = field.getAnnotation(Value.class);
			
			if (autowired == null && value == null) {
				continue;
			}
			
			// 参数不能同时标有@Value和@Autowired
			if (value != null && autowired != null) {
				throw new BeanInjectionException(
						String.format("Cannot specify both @Autowired and @Value when inject %s bean into %s@%s.", field.getType().getName(), beanName, clazz.getName()));
			}
			
			field.setAccessible(true);
			if (value != null) {
				field.set(instance, propertyResolver.getProperty(value.value(), field.getType()));
			} else {
				Object bean = autowired.name().isEmpty() ? getBean(autowired.name()) : getBean(field.getType());
				if (bean == null && autowired.required()) {
					throw new BeanInjectionException(String.format("No such bean %s@%s to inject to bean %s.", autowired.name(), field.getType().getName(), beanName));
				} else if (bean != null) {
					if (field.getType().isAssignableFrom(bean.getClass())) {
						field.set(instance, bean);
					} else {
						throw new BeanInjectionException(String.format("Bean %s@%s cannot be assigned to type %s field.",
								ClassUtil.findBeanName(bean.getClass()), bean.getClass().getName(), field.getType().getName()));
					}
				}
			}
		}
		
		// setter方式注入
		for (Method method : findSetters(clazz)) {
			Autowired autowired = method.getAnnotation(Autowired.class);
			Parameter param = method.getParameters()[0];
			if (autowired == null) {
				autowired = param.getAnnotation(Autowired.class);
			}
			
			Object bean;
			if (autowired != null) {
				String name = autowired.name();
				bean = name.isEmpty() ? getBean(param.getType()) : getBean(name);
			} else {
				bean = getBean(param.getType());
			}
			
			if (bean == null && (autowired == null || autowired.required())) {
				throw new BeanInjectionException(String.format("No %s@%s bean can be injected to the property of %s.",
						autowired == null ? "" : autowired.name(), param.getType().getName(), clazz.getName()));
			}
			
			if (bean != null) {
				method.invoke(instance, bean);
			}
		}
	}
	
	// 必须符合setXXX格式，XXX为类的属性名且第一个字符为大写
	private List<Method> findSetters(Class<?> clazz) {
		String prefix = "set";
		Map<String, Class<?>> setterNameToType = new HashMap<>();
		List<Method> setters = new ArrayList<>();
		
		for (Field field : clazz.getDeclaredFields()) {
			int mod = field.getModifiers();
			if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
				continue;
			}
			
			String name = prefix + Character.toLowerCase(field.getName().charAt(0)) + field.getName().substring(1);
			setterNameToType.put(name, field.getType());
		}
		
		for (Method method : clazz.getDeclaredMethods()) {
			Class<?> type = setterNameToType.get(method.getName());
			Parameter[] params = method.getParameters();
			
			if (type != null && params.length == 1
						&& type.isAssignableFrom(params[0].getType())) {
				setters.add(method);
			}
		}
		
		return setters;
	}
	
	private void instantiateBeans() {
		beanDefinitionMap.values().stream()
				.filter(def -> def.getClazz().isAnnotationPresent(Configuration.class))
				.forEach(this::createBeanAsEarlySingleton);

		List<BeanPostProcessor> processors = beanDefinitionMap.values().stream()
				.filter(def -> BeanPostProcessor.class.isAssignableFrom(def.getClazz()))
				.sorted()
				.map(def -> (BeanPostProcessor) createBeanAsEarlySingleton(def))
				.toList();
		beanPostProcessors.addAll(processors);
		
		beanDefinitionMap.values().stream()
				.filter(def -> def.getInstance() == null)
				.sorted()
				.forEach(def -> {
					if (def.getInstance() == null) {
						createBeanAsEarlySingleton(def);
					}
				});
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getBean(String beanName) {
		BeanDefinition def = findBeanDefinition(beanName);
		if (def == null) {
			return null;
		}

		if (def.getInstance() == null) {
			createBean(def);
		}
		return (T) def.getInstance();
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getBean(Class<T> type) {
		BeanDefinition def = findBeanDefinition(type);
		if (def == null) {
			return null;
		}

		if (def.getInstance() == null) {
			createBean(def);
		}
		return (T) def.getInstance();
	}

	private Object createBeanAsEarlySingleton(BeanDefinition def) {
		if (!registeredBeanNames.add(def.getBeanName())) {
			throw new UnsatisfiedDependencyException(String.format("The name %s has been used.", def.getBeanName()));
		}

		if (!creatingBeanNames.add(def.getBeanName())) {
			throw new UnsatisfiedDependencyException(String.format("Cannot create bean %s since it is being created. There may be cyclic conflicts.", def.getBeanName()));
		}

		Executable createFunc = def.getFactoryMethod() == null ?
				def.getConstructor() : def.getFactoryMethod();

		Parameter[] params = createFunc.getParameters();
		Object[] args = new Object[params.length];

		for (int i = 0; i < params.length; i++) {
			Value value = params[i].getAnnotation(Value.class);
			Autowired autowired = params[i].getAnnotation(Autowired.class);

			// Configuration的bean是工厂，不能通过Autowired进行注入
			if (isConfigurationBean(def) && value == null) {
				throw new BeanCreationException(String.format("Cannot specify @Autowired when create @Configuration bean '%s': %s.", def.getBeanName(), def.getClazz().getName()));
			}
			
			// 参数不能同时标有@Value和@Autowired
			if (value != null && autowired != null) {
				throw new BeanCreationException(
						String.format("Cannot specify both @Autowired and @Value when create bean '%s': %s.", def.getBeanName(), def.getClazz().getName()));
			}
			
			if (value != null) {
				args[i] = propertyResolver.getProperty(value.value(), params[i].getType());
			} else if (autowired == null) {
				BeanDefinition beanDef = findBeanDefinition(params[i].getType());
				if (beanDef == null) {
					throw new BeanCreationException(String.format("No such %s type bean.", params[i].getType()));
				}
				
				if (beanDef.getInstance() == null) {
					createBeanAsEarlySingleton(beanDef);
				}
				args[i] = beanDef.getInstance();
			} else {
				String name = autowired.name();
				BeanDefinition beanDef;
				
				if (name.isEmpty()) {
					beanDef = findBeanDefinition(params[i].getType());
				} else {
					beanDef = findBeanDefinition(name, params[i].getType());
				}
				
				if (beanDef == null && autowired.required()) {
					throw new BeanCreationException(String.format("Missing autowired bean with type '%s' when create bean '%s': %s.", params[i].getType().getName(),
							def.getBeanName(), def.getClazz().getName()));
				}
				
				if (beanDef != null) {
					if (beanDef.getInstance() == null) {
						createBeanAsEarlySingleton(beanDef);
					}
					args[i] = beanDef.getInstance();
				} else {
					args[i] = null;
				}
			}
		}
		
		Object instance = null;
		try {
			if (def.getFactoryMethod() != null) {
				Method method = def.getFactoryMethod();
				Object configInstance = findBeanDefinition(method.getDeclaringClass()).getInstance();
				instance = method.invoke(configInstance, args);
			} else {
				instance = def.getConstructor().newInstance(args);
			}
		} catch (ReflectiveOperationException e) {
			throw new BeanCreationException(e);
		}
		
		def.setInstance(instance);

		creatingBeanNames.remove(def.getBeanName());

		return def.getInstance();
	}
	
	private boolean isConfigurationBean(BeanDefinition def) {
		return ClassUtil.findAnnotation(def.getClazz(), Configuration.class) != null;
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
						ClassUtil.findAnnotation(clazz, Lazy.class) != null,
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
						method.isAnnotationPresent(Lazy.class),
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
	
	public BeanDefinition findBeanDefinition(String name, Class<?> type) {
		BeanDefinition def = findBeanDefinition(name);
		
		if (def != null && !type.isAssignableFrom(def.getClazz())) {
			throw new BeanTypeNotConsistentException(String.format("The type of bean %s is not %s, but is %s.", name, type.getName(), def.getClazz().getName()));
		}
		
		return def;
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
