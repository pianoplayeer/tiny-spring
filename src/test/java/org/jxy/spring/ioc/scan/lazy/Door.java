package org.jxy.spring.ioc.scan.lazy;

import org.jxy.spring.ioc.annotation.Bean;
import org.jxy.spring.ioc.annotation.Component;
import org.jxy.spring.ioc.annotation.Lazy;

@Component
public class Door {
    @Bean
    @Lazy
    public Door lazyDoor() {
        return new Door();
    }
}
