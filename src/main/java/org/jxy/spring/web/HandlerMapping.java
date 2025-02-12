package org.jxy.spring.web;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

public class HandlerMapping {
    private List<Handler> getHandlers = new ArrayList<>();
    
    private List<Handler> postHandlers = new ArrayList<>();

    private List<HandlerInterceptor> interceptors = new ArrayList<>();

    public HandlerExecutionChain getHandlerChain(HttpServletRequest request) {
        HandlerExecutionChain chain = new HandlerExecutionChain();
        
    }
}
