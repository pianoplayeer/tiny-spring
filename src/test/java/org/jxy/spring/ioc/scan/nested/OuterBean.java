package org.jxy.spring.ioc.scan.nested;

import org.jxy.spring.annotation.Component;

@Component
public class OuterBean {

    @Component
    public static class NestedBean {

    }
}
