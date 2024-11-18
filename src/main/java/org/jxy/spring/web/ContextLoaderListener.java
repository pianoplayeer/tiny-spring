package org.jxy.spring.web;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import org.jxy.spring.exception.ContextInitializationException;
import org.jxy.spring.ioc.context.AnnotationConfigApplicationContext;
import org.jxy.spring.ioc.context.ApplicationContext;
import org.jxy.spring.ioc.resolver.PropertyResolver;
import org.jxy.spring.utils.WebUtils;


public class ContextLoaderListener implements ServletContextListener {
    private static final String ENCODING_PROPERTY = "${spring.web.character-encoding:UTF-8}";

    private static final String APPLICATION_CONTEXT = "applicationContext";

    private static final String DISPATCHER_SERVLET = "dispatcherServlet";

    private static final String CONFIG_PARAM = "config";

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        var servletContext = servletContextEvent.getServletContext();

        var propertyResolver = WebUtils.createPropertyResolver();
        String encoding = propertyResolver.getProperty(ENCODING_PROPERTY);
        servletContext.setRequestCharacterEncoding(encoding);
        servletContext.setResponseCharacterEncoding(encoding);

        var applicationContext = createApplicationContext(servletContext.getInitParameter(CONFIG_PARAM), propertyResolver);
        servletContext.setAttribute(APPLICATION_CONTEXT, applicationContext);

        var dispatcherServlet = new DispatcherServlet();
        var dispatcherReg = servletContext.addServlet(DISPATCHER_SERVLET, dispatcherServlet);
        dispatcherReg.addMapping("/");
        dispatcherReg.setLoadOnStartup(0);
    }

    private ApplicationContext createApplicationContext(String configClassName, PropertyResolver propertyResolver) {
        Class<?> clazz = null;

        if (configClassName == null) {
            throw new ContextInitializationException("Config class name cannot be null.");
        }

        try {
            clazz = Class.forName(configClassName);
        } catch (ReflectiveOperationException e) {
            throw new ContextInitializationException(e);
        }

        return new AnnotationConfigApplicationContext(clazz, propertyResolver);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        var servletContext = servletContextEvent.getServletContext();
        ApplicationContext applicationContext = (ApplicationContext) servletContext.getAttribute(APPLICATION_CONTEXT);

        applicationContext.close();
    }
}
