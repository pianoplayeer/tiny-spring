package org.jxy.spring.jdbc.mapper;

import lombok.Getter;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BooleanRowMapper implements RowMapper<Boolean> {
    @Getter
    private static final BooleanRowMapper instance = new BooleanRowMapper();

    @Override
    public Boolean mapRow(ResultSet rs, int rowNum) throws SQLException {
        return rs.getBoolean(1);
    }
}
