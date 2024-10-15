package org.jxy.spring.utils;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @date 2024/9/15
 * @package org.jxy.utils
 */
public class YmlUtil {
	@SuppressWarnings("unchecked")
	public static Map<String, Object> loadYml(InputStream inputStream) {
		Yaml yaml = new Yaml();
		return (Map<String, Object>) yaml.load(inputStream);
	}
	
	public static Map<String, Object> loadYmlAsPlainMap(InputStream inputStream) {
		var map = loadYml(inputStream);
		Map<String, Object> plainMap = new HashMap<>();
		
		convertToPlain(map, plainMap, "");
		return plainMap;
	}
	
	@SuppressWarnings("unchecked")
	private static void convertToPlain(Map<String, Object> sourceMap,
								  Map<String, Object> targetMap,
								  String prefix) {
		sourceMap.forEach((k, v) -> {
			if (v instanceof Map) {
				convertToPlain((Map<String, Object>) v, targetMap, prefix + k + ".");
			} else if (v instanceof List) {
				targetMap.put(prefix + k, v);
			} else {
				targetMap.put(prefix + k, v == null ? "" : v.toString());
			}
		});
	}
}
