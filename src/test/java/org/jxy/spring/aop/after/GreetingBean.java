package org.jxy.spring.aop.after;


import org.jxy.spring.annotation.Aspect;
import org.jxy.spring.annotation.Component;

@Component
@Aspect("politeInvocationHandler")
public class GreetingBean {

    public String hello(String name) {
        return "Hello, " + name + ".";
    }

    public String morning(String name) {
        return "Morning, " + name + ".";
    }
}
