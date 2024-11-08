package org.jxy.spring.ioc.scan.primary;


import org.jxy.spring.annotation.Bean;
import org.jxy.spring.annotation.Configuration;
import org.jxy.spring.annotation.Primary;

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
