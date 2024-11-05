package org.jxy.spring.aop.circular;

import org.junit.jupiter.api.Test;
import org.jxy.spring.ioc.context.AnnotationConfigApplicationContext;
import org.jxy.spring.ioc.resolver.PropertyResolver;

import java.util.Properties;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CircularTest {
    @Test
    public void testCircular() {
        try (var ctx = new AnnotationConfigApplicationContext(Application.class, createPropertyResolver())) {
            A a = ctx.getBean(A.class);
            B b = ctx.getBean(B.class);

            assert a.getB() == b;
            assert b.getA() == a;
            System.out.println("A: " + a);
            System.out.println("B: " + b);
        }
    }

    PropertyResolver createPropertyResolver() {
        var ps = new Properties();
        var pr = new PropertyResolver(ps);
        return pr;
    }
}
