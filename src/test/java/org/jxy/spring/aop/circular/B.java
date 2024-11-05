package org.jxy.spring.aop.circular;

import lombok.Getter;
import org.jxy.spring.aop.annotation.Aspect;
import org.jxy.spring.ioc.annotation.Autowired;
import org.jxy.spring.ioc.annotation.Component;

@Component
@Aspect("handlerB")
public class B {
    @Autowired
    @Getter
    private A a;
}
