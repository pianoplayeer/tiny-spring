package org.jxy.spring.jdbc;

import org.junit.jupiter.api.BeforeEach;
import org.jxy.spring.ioc.resolver.PropertyResolver;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class JdbcTestBase {

    public static final String CREATE_USER = "CREATE TABLE users (id INTEGER PRIMARY KEY AUTO_INCREMENT, name VARCHAR(255) NOT NULL, age INTEGER) ENGINE=InnoDB DEFAULT CHARSET=utf8";
    public static final String CREATE_ADDRESS = "CREATE TABLE addresses (id INTEGER PRIMARY KEY AUTO_INCREMENT, userId INTEGER NOT NULL, address VARCHAR(255) NOT NULL, zip INTEGER) ENGINE=InnoDB DEFAULT CHARSET=utf8";

    public static final String INSERT_USER = "INSERT INTO users (name, age) VALUES (?, ?)";
    public static final String INSERT_ADDRESS = "INSERT INTO addresses (userId, address, zip) VALUES (?, ?, ?)";

    public static final String UPDATE_USER = "UPDATE users SET name = ?, age = ? WHERE id = ?";
    public static final String UPDATE_ADDRESS = "UPDATE addresses SET address = ?, zip = ? WHERE id = ?";

    public static final String DELETE_USER = "DELETE FROM users WHERE id = ?";
    public static final String DELETE_ADDRESS_BY_USERID = "DELETE FROM addresses WHERE userId = ?";

    public static final String DROP_USER = "DROP TABLE IF EXISTS users";
    public static final String DROP_ADDRESS = "DROP TABLE IF EXISTS addresses";

    public static final String SELECT_USER = "SELECT * FROM users WHERE id = ?";
    public static final String SELECT_USER_NAME = "SELECT name FROM users WHERE id = ?";
    public static final String SELECT_USER_AGE = "SELECT age FROM users WHERE id = ?";
    public static final String SELECT_ADDRESS_BY_USERID = "SELECT * FROM addresses WHERE userId = ?";

    public PropertyResolver createPropertyResolver() {
        var ps = new Properties();
        ps.put("spring.datasource.url", "jdbc:mysql://localhost:3306/tiny_spring_test");
        ps.put("spring.datasource.username", "root");
        ps.put("spring.datasource.password", "");
        ps.put("spring.datasource.driver-class-name", "com.mysql.cj.jdbc.Driver");
        var pr = new PropertyResolver(ps);
        return pr;
    }

}
