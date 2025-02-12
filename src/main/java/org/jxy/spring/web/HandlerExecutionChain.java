package org.jxy.spring.web;

import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class HandlerExecutionChain {
    @Getter
    private Handler handler;

    private List<HandlerInterceptor> interceptorChain = new ArrayList<>();
    
    private int interceptorIndex = -1;
    
    public HandlerExecutionChain(Handler handler, List<HandlerInterceptor> interceptors) {
        this.handler = handler;
        if (interceptors != null) {
            interceptorChain.addAll(interceptors);
        }
    }
    
    public boolean applyPreHandle(HttpServletRequest request, HttpServletResponse response) throws Exception {
        for (int i = 0; i < interceptorChain.size(); i++) {
            HandlerInterceptor interceptor = interceptorChain.get(i);
            
            if (!interceptor.preHandle(request, response, handler)) {
                triggerAfterCompletion(request, response, null);
                return false;
            }
            interceptorIndex = i;
        }
        
        return true;
    }
    
    public void applyPostHandle(HttpServletRequest request, HttpServletResponse response, @Nullable ModelAndView mv)
        throws Exception {
        
        for (int i = interceptorChain.size() - 1; i >= 0; i--) {
            HandlerInterceptor interceptor = interceptorChain.get(i);
            interceptor.postHandle(request, response, handler, mv);
        }
    }
    
    public void triggerAfterCompletion(HttpServletRequest request, HttpServletResponse response, @Nullable Exception ex) {
        for (int i = interceptorIndex; i >= 0; i--) {
            HandlerInterceptor interceptor = interceptorChain.get(i);
            
            try {
                interceptor.afterCompletion(request, response, handler, ex);
            } catch (Throwable ex2) {
                log.error("HandlerInterceptor.afterCompletion threw exception", ex2);
            }
        }
    }
}
