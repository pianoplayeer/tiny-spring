package org.jxy.spring.jdbc.with.tx;


import org.jxy.spring.annotation.ComponentScan;
import org.jxy.spring.annotation.Configuration;
import org.jxy.spring.annotation.Import;
import org.jxy.spring.jdbc.JdbcConfiguration;

@ComponentScan
@Configuration
@Import(JdbcConfiguration.class)
public class JdbcWithTxApplication {

}
