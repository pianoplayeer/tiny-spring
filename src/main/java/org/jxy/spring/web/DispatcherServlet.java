package org.jxy.spring.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jxy.spring.ioc.context.ApplicationContext;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class DispatcherServlet extends HttpServlet {
    private static final String HANDLER_MAPPING_BEAN_NAME = "handlerMapping";

    private final HandlerMapping handlerMapping;

    public DispatcherServlet(ApplicationContext applicationContext) {
        handlerMapping = applicationContext.getBean(HANDLER_MAPPING_BEAN_NAME);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    
    }
    
    private HandlerExecutionChain getHandlerChain(HttpServletRequest request) {
        return handlerMapping.getHandlerChain(request);
    }
}
