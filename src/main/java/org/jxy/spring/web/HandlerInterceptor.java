package org.jxy.spring.web;

import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public interface HandlerInterceptor {
    
    default boolean preHandle(HttpServletRequest request, HttpServletResponse response, Handler handler)
            throws Exception {
        
        return true;
    }
    
    default void postHandle(HttpServletRequest request, HttpServletResponse response, Handler handler,
                            @Nullable ModelAndView modelAndView)
            throws Exception {
    }
    
    default void afterCompletion(HttpServletRequest request, HttpServletResponse response, Handler handler,
                                 @Nullable Exception ex) throws Exception {
    }
    
    default List<String> includePaths() {
        return null;
    }
    
    default List<String> excludePaths() {
        return null;
    }
}
