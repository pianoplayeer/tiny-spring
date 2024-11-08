package org.jxy.spring.aop.circular;

import lombok.Getter;
import org.jxy.spring.annotation.Aspect;
import org.jxy.spring.annotation.Autowired;
import org.jxy.spring.annotation.Component;

@Component
@Aspect("handlerA")
public class A {
    @Autowired
    @Getter
    private B b;
}
