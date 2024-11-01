package org.jxy.spring.ioc.scan.proxy;


import org.jxy.spring.ioc.annotation.Autowired;
import org.jxy.spring.ioc.annotation.Component;

@Component
public class InjectProxyOnConstructorBean {

    public final OriginBean injected;

    public InjectProxyOnConstructorBean(@Autowired OriginBean injected) {
        this.injected = injected;
    }
}
