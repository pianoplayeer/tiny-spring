package org.jxy.spring.jdbc;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jxy.spring.annotation.Bean;
import org.jxy.spring.annotation.Configuration;
import org.jxy.spring.annotation.Value;
import org.jxy.spring.aop.processor.AopAnnotationBeanPostProcessor;
import org.jxy.spring.jdbc.transaction.DataSourceTransactionManager;
import org.jxy.spring.jdbc.transaction.PlatformTransactionManager;
import org.jxy.spring.jdbc.transaction.TransactionalBeanPostProcessor;

import javax.sql.DataSource;

@Configuration
public class JdbcConfiguration {
    @Bean(destroyMethod = "close")
    public DataSource dataSource(
        @Value("${spring.datasource.url}") String url,
        @Value("${spring.datasource.username}") String username,
        @Value("${spring.datasource.password}") String password,
        @Value("${spring.datasource.driver-class-name:}") String driver,
        @Value("${spring.datasource.maximum-pool-size:20}") int maximumPoolSize,
        @Value("${spring.datasource.minimum-pool-size:1}") int minimumPoolSize,
        @Value("${spring.datasource.connection-timeout:30000}") int connTimeout   
    ) {
        var config = new HikariConfig();
        config.setUsername(username);
        config.setPassword(password);
        config.setJdbcUrl(url);
        config.setMaximumPoolSize(maximumPoolSize);
        config.setMinimumIdle(minimumPoolSize);
        config.setConnectionTimeout(connTimeout);
        config.setAutoCommit(false);

        if (driver != null) {
            config.setDriverClassName(driver);
        }

        return new HikariDataSource(config);
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public TransactionalBeanPostProcessor transactionalBeanPostProcessor() {
        return new TransactionalBeanPostProcessor();
    }

    @Bean
    public PlatformTransactionManager platformTransactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
