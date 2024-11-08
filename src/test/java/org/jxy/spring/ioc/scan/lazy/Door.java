package org.jxy.spring.ioc.scan.lazy;

import org.jxy.spring.annotation.Bean;
import org.jxy.spring.annotation.Component;
import org.jxy.spring.annotation.Lazy;

@Component
public class Door {
    @Bean
    @Lazy
    public Door lazyDoor() {
        return new Door();
    }
}
