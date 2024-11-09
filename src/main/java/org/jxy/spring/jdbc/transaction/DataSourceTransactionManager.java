package org.jxy.spring.jdbc.transaction;

import org.jxy.spring.exception.TransactionException;

import javax.sql.DataSource;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;

public class DataSourceTransactionManager implements PlatformTransactionManager, InvocationHandler {
    private final static ThreadLocal<TransactionStatus> txStatusThreadLocal = new ThreadLocal<>();

    private final DataSource dataSource;

    public DataSourceTransactionManager(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static Connection getCurrentConnection() {
        TransactionStatus status = txStatusThreadLocal.get();
        return status == null ? null : status.getConnection();
    }
    
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        TransactionStatus status = txStatusThreadLocal.get();

        if (status != null) {
            return method.invoke(proxy, args);
        } else {
            boolean autoCommit = false;
            Connection connection = null;
            
            try (Connection conn = dataSource.getConnection()) {
                autoCommit = conn.getAutoCommit();
                connection = conn;
                
                if (autoCommit) {
                    connection.setAutoCommit(false);
                }
                
                txStatusThreadLocal.set(new TransactionStatus(connection));
                Object result = method.invoke(proxy, args);
                conn.commit();
                
                return result;
            } catch (InvocationTargetException e) {
                TransactionException te = new TransactionException(e);
                try {
                    connection.rollback();
                } catch (SQLException sqle) {
                    te.addSuppressed(sqle);
                }
                
                throw te;
            } finally {
                if (autoCommit) {
                    connection.setAutoCommit(true);
                }
                txStatusThreadLocal.remove();
            }
        }
    }
}
