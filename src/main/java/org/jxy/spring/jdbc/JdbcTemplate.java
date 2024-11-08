package org.jxy.spring.jdbc;

import lombok.AllArgsConstructor;
import org.jxy.spring.exception.DataAccessException;
import org.jxy.spring.jdbc.mapper.*;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class JdbcTemplate {
    private final DataSource dataSource;

    public <T> T queryForObject(String sql, Class<T> clazz, Object... args) throws DataAccessException {

        return queryForObject(sql, getRowMapper(clazz), args);
    }

    public <T> T queryForObject(String sql, RowMapper<T> rowMapper, Object... args) throws DataAccessException {
        return execute(
                preparedStatementCreator(sql, args),
                ps -> {
                    T t = null;
                    try (ResultSet set = ps.executeQuery()) {
                        while (set.next()) {
                            if (t == null) {
                                t = rowMapper.mapRow(set, set.getRow());
                            } else {
                                throw new DataAccessException("Multiple rows found.");
                            }
                        }
                    }

                    if (t == null) {
                        throw new DataAccessException("Empty result set.");
                    }

                    return t;
                }
        );
    }

    public <T> List<T> queryForList(String sql, Class<T> clazz, Object... args) throws DataAccessException {
        return queryForList(sql, getRowMapper(clazz), args);
    }

    public <T> List<T> queryForList(String sql, RowMapper<T> rowMapper, Object... args) throws DataAccessException {
        return execute(
                preparedStatementCreator(sql, args),
                ps -> {
                    List<T> list = new ArrayList<>();
                    try (ResultSet set = ps.executeQuery()) {
                        while (set.next()) {
                            list.add(rowMapper.mapRow(set, set.getRow()));
                        }
                    }

                    return list;
                }
        );
    }

    public int update(String sql, Object... args) throws DataAccessException {
        return execute(preparedStatementCreator(sql, args), PreparedStatement::executeUpdate);
    }

    public Number updateAndReturnGeneratedKey(String sql, Object... args) throws DataAccessException {
        return execute(
                conn -> {
                    var ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    bindArgs(ps, args);
                    return ps;
                },
                ps -> {
                    int n = ps.executeUpdate();

                    if (n == 0) {
                        throw new DataAccessException("0 rows inserted.");
                    }
                    if (n > 1) {
                        throw new DataAccessException("Multiple rows inserted.");
                    }

                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (keys.next()) {
                            return (Number) keys.getObject(1);
                        }
                    }
                    throw new DataAccessException("Keys overflow.");
                }
        );
    }

    public <T> T execute(ConnectionCallback<T> action) {
        try (Connection conn = dataSource.getConnection()) {
            boolean autoCommit = conn.getAutoCommit();

            if (!autoCommit) {
                conn.setAutoCommit(true);
            }
            T result = action.doInConnection(conn);
            if (!autoCommit) {
                conn.setAutoCommit(false);
            }

            return result;
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    public <T> T execute(PreparedStatementCreator creator, PreparedStatementCallback<T> action) {
        return execute(conn -> {
            try (PreparedStatement statement = creator.createPreparedStatement(conn)) {
                return action.doInPreparedStatement(statement);
            }
        });
    }

    private PreparedStatementCreator preparedStatementCreator(String sql, Object... args) {
        return conn -> {
            var ps = conn.prepareStatement(sql);
            bindArgs(ps, args);
            return ps;
        };
    }

    private void bindArgs(PreparedStatement statement, Object... args) throws SQLException {
        for (int i = 0; i < args.length; i++) {
            statement.setObject(i + 1, args[i]);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> RowMapper<T> getRowMapper(Class<T> clazz) {
        if (clazz == String.class) {
            return (RowMapper<T>) StringRowMapper.getInstance();
        } else if (clazz == Boolean.class || clazz == boolean.class) {
            return (RowMapper<T>) BooleanRowMapper.getInstance();
        } else if (Number.class.isAssignableFrom(clazz) || clazz.isPrimitive()) {
            return (RowMapper<T>) NumberRowMapper.getInstance();
        } else {
            return new BeanRowMapper<>(clazz);
        }
    }
}
