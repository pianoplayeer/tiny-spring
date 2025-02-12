package org.jxy.spring.web;

import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @date 2025/1/25
 * @package org.jxy.spring.web
 */
@Getter
public class ModelAndView {
	private String viewName;
	
	private Map<String, Object> model;
	
	private int status;
	
	public ModelAndView(String viewName) {
		this(viewName, HttpServletResponse.SC_OK, null);
	}
	
	public ModelAndView(String viewName, @Nullable Map<String, Object> model) {
		this(viewName, HttpServletResponse.SC_OK, model);
	}
	
	public ModelAndView(String viewName, int status) {
		this(viewName, status, null);
	}
	
	public ModelAndView(String viewName, int status, @Nullable Map<String, Object> model) {
		this.viewName = viewName;
		this.status = status;
		if (model != null) {
			addModel(model);
		}
	}
	
	public ModelAndView(String viewName, String modelName, Object modelObject) {
		this(viewName, HttpServletResponse.SC_OK, null);
		addModel(modelName, modelObject);
	}
	
	public void addModel(Map<String, Object> map) {
		if (this.model == null) {
			this.model = new HashMap<>();
		}
		this.model.putAll(map);
	}
	
	public void addModel(String key, Object value) {
		if (this.model == null) {
			this.model = new HashMap<>();
		}
		this.model.put(key, value);
	}
}
