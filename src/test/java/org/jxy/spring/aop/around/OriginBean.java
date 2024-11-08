package org.jxy.spring.aop.around;

import org.jxy.spring.annotation.Aspect;
import org.jxy.spring.annotation.Component;
import org.jxy.spring.annotation.Value;

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
