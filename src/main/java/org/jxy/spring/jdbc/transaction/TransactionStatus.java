package org.jxy.spring.jdbc.transaction;


import java.sql.Connection;

public record TransactionStatus(Connection connection) {
}
