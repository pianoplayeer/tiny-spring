package org.jxy.spring.aop.around;

import org.jxy.spring.annotation.Autowired;
import org.jxy.spring.annotation.Component;
import org.jxy.spring.annotation.Order;

@Order(0)
@Component
public class OtherBean {

    public OriginBean origin;

    public OtherBean(@Autowired OriginBean origin) {
        this.origin = origin;
    }
}
