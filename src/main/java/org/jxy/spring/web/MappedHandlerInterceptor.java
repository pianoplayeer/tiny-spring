package org.jxy.spring.web;

import jakarta.annotation.Nonnull;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jxy.spring.exception.ServerErrorException;
import org.jxy.spring.utils.PathUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @date 2025/2/12
 * @package org.jxy.spring.web
 */
@Slf4j
public class MappedHandlerInterceptor implements HandlerInterceptor {
	
	private HandlerInterceptor interceptor;
	
	private List<Pattern> excludePatterns = new ArrayList<>();
	
	private List<Pattern> includePatterns = new ArrayList<>();
	
	public MappedHandlerInterceptor(HandlerInterceptor interceptor) {
		this.interceptor = interceptor;
		init(interceptor.includePaths(), interceptor.excludePaths());
	}
	
	private void init(List<String> includePaths, List<String> excludePaths) {
		if (includePaths != null) {
			includePaths.forEach(path -> {
				try {
					includePatterns.add(PathUtils.compile(path));
				} catch (ServletException e) {
					throw new ServerErrorException(e);
				}
			});
		}
		
		if (excludePaths != null) {
			excludePaths.forEach(path -> {
				try {
					excludePatterns.add(PathUtils.compile(path));
				} catch (ServletException e) {
					throw new ServerErrorException(e);
				}
			});
		}
	}
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Handler handler) throws Exception {
		return interceptor.preHandle(request, response, handler);
	}
	
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Handler handler, ModelAndView modelAndView) throws Exception {
		interceptor.postHandle(request, response, handler, modelAndView);
	}
	
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Handler handler, Exception ex) throws Exception {
		interceptor.afterCompletion(request, response, handler, ex);
	}
}
