package org.jxy.spring.jdbc.transaction;

import javax.sql.DataSource;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;

public class DataSourceTransactionManager implements PlatformTransactionManager, InvocationHandler {
    private final ThreadLocal<TransactionStatus> txStatusThreadLocal = new ThreadLocal<>();

    private final DataSource dataSource;

    public DataSourceTransactionManager(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        TransactionStatus status = txStatusThreadLocal.get();

        if (status != null) {
            method.invoke(proxy, args);
        } else {
            boolean auto
            try (Connection conn = dataSource.getConnection()) {

            } catch (InvocationTargetException e) {

            } finally {
                if ()
            }
        }
    }
}
