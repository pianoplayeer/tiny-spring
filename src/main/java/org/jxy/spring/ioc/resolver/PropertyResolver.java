package org.jxy.spring.ioc.resolver;

import lombok.extern.slf4j.Slf4j;

import java.time.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

/**
 * @date 2024/9/15
 * @package org.jxy.resolver
 */
@Slf4j
public class PropertyResolver {
	private HashMap<String, String> properties = new HashMap<>();
	
	private Map<Class<?>, Function<String, Object>> converters = new HashMap<>();
	
	public PropertyResolver(Properties props) {
		log.info("prepare properties...");
		properties.putAll(System.getenv());
		
		for (String name : props.stringPropertyNames()) {
			properties.put(name, props.getProperty(name));
		}
		
		// register converters:
		converters.put(String.class, s -> s);
		converters.put(boolean.class, Boolean::parseBoolean);
		converters.put(Boolean.class, Boolean::valueOf);
		
		converters.put(byte.class, Byte::parseByte);
		converters.put(Byte.class, Byte::valueOf);
		
		converters.put(short.class, Short::parseShort);
		converters.put(Short.class, Short::valueOf);
		
		converters.put(int.class, Integer::parseInt);
		converters.put(Integer.class, Integer::valueOf);
		
		converters.put(long.class, Long::parseLong);
		converters.put(Long.class, Long::valueOf);
		
		converters.put(float.class, Float::parseFloat);
		converters.put(Float.class, Float::valueOf);
		
		converters.put(double.class, Double::parseDouble);
		converters.put(Double.class, Double::valueOf);
		
		converters.put(LocalDate.class, LocalDate::parse);
		converters.put(LocalTime.class, LocalTime::parse);
		converters.put(LocalDateTime.class, LocalDateTime::parse);
		converters.put(ZonedDateTime.class, ZonedDateTime::parse);
		converters.put(Duration.class, Duration::parse);
		converters.put(ZoneId.class, ZoneId::of);
	}
	
	public void registerConverters(Map<Class<?>, Function<String, Object>> converterMap) {
		if (converterMap != null) {
			converters.putAll(converterMap);
		}
	}
	
	public <T> T getProperty(String key, Class<T> clazz) {
		String value = getProperty(key);
		if (value == null) {
			return null;
		}
		
		return convert(value, clazz);
	}
	
	public String getProperty(String key) {
		return getProperty(key, true);
	}
	
	@SuppressWarnings("unchecked")
	private <T> T convert(String value, Class<?> clazz) {
		var func = converters.get(clazz);
		if (func == null) {
			throw new IllegalArgumentException("No converter matches the class.");
		}
		return (T) func.apply(value);
	}
	
	/**
	 * @param key
	 * @param required 为true解析不到直接返回null，false返回key本身
	 * @return
	 */
	private String getProperty(String key, boolean required) {
		PropertyExpr expr = parsePropertyExpr(key);
		
		if (expr != null) {
			if (expr.defaultValue() == null) {
				return getProperty(expr.name(), required);
			} else {
				return getProperty(expr.name(), expr.defaultValue());
			}
		}
		
		String value = properties.get(key);
		if (value != null) {
			return parseValue(value);
		}
		
		if (required) {
			return null;
		}
		return key;
	}
	
	private String parseValue(String value) {
		if (value.startsWith("${") && value.endsWith("}")) {
			return getProperty(value, false);
		}
		return value;
	}
	
	private String getProperty(String name, String defaultVal) {
		String value = getProperty(name, true);
		
		if (value == null) {
			return parseValue(defaultVal);
		}
		return value;
	}
	
	private PropertyExpr parsePropertyExpr(String key) {
		if (key.startsWith("${") && key.endsWith("}")) {
			int spliter = key.indexOf(":");
			if (spliter == -1) {
				return new PropertyExpr(key.substring(2, key.length() - 1), null);
			} else {
				return new PropertyExpr(key.substring(2, spliter), key.substring(spliter + 1, key.length() - 1));
			}
		}
		
		return null;
	}
}

record PropertyExpr(String name, String defaultValue) {
}
