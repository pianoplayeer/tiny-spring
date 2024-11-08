package org.jxy.spring.ioc.imported;

import org.jxy.spring.annotation.Bean;
import org.jxy.spring.annotation.Configuration;

import java.time.ZonedDateTime;

@Configuration
public class ZonedDateConfiguration {

    @Bean
    ZonedDateTime startZonedDateTime() {
        return ZonedDateTime.now();
    }
}
