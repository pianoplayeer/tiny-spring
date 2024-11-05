package org.jxy.spring.aop.circular;

import lombok.Getter;
import org.jxy.spring.aop.annotation.Aspect;
import org.jxy.spring.ioc.annotation.Autowired;
import org.jxy.spring.ioc.annotation.Component;

@Component
@Aspect("handlerA")
public class A {
    @Autowired
    @Getter
    private B b;
}
