package org.jxy.spring.aop.before;

import org.junit.jupiter.api.Test;
import org.jxy.spring.ioc.context.AnnotationConfigApplicationContext;
import org.jxy.spring.ioc.resolver.PropertyResolver;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BeforeProxyTest {


    public static void testBeforeProxy() {
        try (var ctx = new AnnotationConfigApplicationContext(BeforeApplication.class, createPropertyResolver())) {
            BusinessBean proxy = ctx.getBean(BusinessBean.class);
            // should print log:
            assertEquals("Hello, Bob.", proxy.hello("Bob"));
            assertEquals("Morning, Alice.", proxy.morning("Alice"));
        }
    }

    static PropertyResolver createPropertyResolver() {
        var ps = new Properties();
        var pr = new PropertyResolver(ps);
        return pr;
    }
}
