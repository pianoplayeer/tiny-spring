package org.jxy.spring.ioc.imported;

import org.jxy.spring.ioc.annotation.Bean;
import org.jxy.spring.ioc.annotation.Configuration;

import java.time.ZonedDateTime;

@Configuration
public class ZonedDateConfiguration {

    @Bean
    ZonedDateTime startZonedDateTime() {
        return ZonedDateTime.now();
    }
}
