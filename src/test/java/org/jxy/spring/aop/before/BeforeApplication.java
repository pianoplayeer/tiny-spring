package org.jxy.spring.aop.before;

import org.jxy.spring.annotation.Bean;
import org.jxy.spring.annotation.ComponentScan;
import org.jxy.spring.annotation.Configuration;
import org.jxy.spring.aop.processor.AopAnnotationBeanPostProcessor;

@Configuration
@ComponentScan
public class BeforeApplication {
    @Bean
    public AopAnnotationBeanPostProcessor aopAnnotationBeanPostProcessor() {
        return new AopAnnotationBeanPostProcessor();
    }
}
