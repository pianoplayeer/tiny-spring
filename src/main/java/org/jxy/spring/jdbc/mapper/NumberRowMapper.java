package org.jxy.spring.jdbc.mapper;

import lombok.Getter;

import java.sql.ResultSet;
import java.sql.SQLException;

public class NumberRowMapper implements RowMapper<Number> {
    @Getter
    private static final NumberRowMapper instance = new NumberRowMapper();

    @Override
    public Number mapRow(ResultSet rs, int rowNum) throws SQLException {
        return (Number) rs.getObject(1);
    }
}
