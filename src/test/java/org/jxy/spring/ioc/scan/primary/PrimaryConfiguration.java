package org.jxy.spring.ioc.scan.primary;


import org.jxy.spring.ioc.annotation.Bean;
import org.jxy.spring.ioc.annotation.Configuration;
import org.jxy.spring.ioc.annotation.Primary;

@Configuration
public class PrimaryConfiguration {

    @Primary
    @Bean
    DogBean husky() {
        return new DogBean("Husky");
    }

    @Bean
    DogBean teddy() {
        return new DogBean("Teddy");
    }
}
