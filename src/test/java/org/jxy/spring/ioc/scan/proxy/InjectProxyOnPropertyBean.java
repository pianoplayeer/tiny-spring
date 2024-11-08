package org.jxy.spring.ioc.scan.proxy;


import org.jxy.spring.annotation.Autowired;
import org.jxy.spring.annotation.Component;

@Component
public class InjectProxyOnPropertyBean {

    @Autowired
    public OriginBean injected;
}
