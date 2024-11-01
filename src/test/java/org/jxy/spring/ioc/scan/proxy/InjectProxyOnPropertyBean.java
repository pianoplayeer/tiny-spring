package org.jxy.spring.ioc.scan.proxy;


import org.jxy.spring.ioc.annotation.Autowired;
import org.jxy.spring.ioc.annotation.Component;

@Component
public class InjectProxyOnPropertyBean {

    @Autowired
    public OriginBean injected;
}
