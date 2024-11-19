package org.jxy.spring.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jxy.spring.ioc.context.ApplicationContext;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class DispatcherServlet extends HttpServlet {
    private static final String HANDLER_MAPPING_BEAN_NAME = "handlerMapping";

    private final HandlerMapping handlerMapping;

    public DispatcherServlet(ApplicationContext applicationContext) {
        handlerMapping = applicationContext.getBean(HANDLER_MAPPING_BEAN_NAME);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter pw = resp.getWriter();
        pw.write("<h1>Hello, world!</h1>");
        pw.flush();
    }
}
