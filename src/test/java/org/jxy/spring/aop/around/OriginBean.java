package org.jxy.spring.aop.around;

import org.jxy.spring.aop.annotation.Aspect;
import org.jxy.spring.ioc.annotation.Component;
import org.jxy.spring.ioc.annotation.Value;

@Component
@Aspect("aroundInvocationHandler")
public class OriginBean {

    @Value("${customer.name}")
    public String name;

    @Polite
    public String hello() {
        return "Hello, " + name + ".";
    }

    public String morning() {
        return "Morning, " + name + ".";
    }
}
