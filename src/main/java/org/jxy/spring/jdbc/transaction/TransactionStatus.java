package org.jxy.spring.jdbc.transaction;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.sql.Connection;

@Getter
public record TransactionStatus(Connection connection) {
}
