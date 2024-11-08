package org.jxy.spring.jdbc.mapper;

import lombok.extern.slf4j.Slf4j;
import org.jxy.spring.exception.DataAccessException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class BeanRowMapper<T> implements RowMapper<T> {
    private Class<T> clazz;

    private Constructor<T> constructor;

    private Map<String, Field> fields = new HashMap<>();

    private Map<String, Method> methods = new HashMap<>();

    public BeanRowMapper(Class<T> clazz) {
        this.clazz = clazz;
        try {
            this.constructor = clazz.getConstructor();
        } catch (ReflectiveOperationException e) {
            throw new DataAccessException(String.format("No public default constructor found for class %s when creating the BeanRowMapper.", clazz.getName()));
        }

        for (Field field : clazz.getFields()) {
            String name = field.getName();
            fields.put(name, field);
            log.debug("Add row mapping for {} : {}", clazz.getName(), name);
        }

        for (Method method : clazz.getMethods()) {
            String name = method.getName();
            Parameter[] params = method.getParameters();

            if (params.length == 1 && name.length() > 3 && name.startsWith("set")) {
                String prop = Character.toLowerCase(name.charAt(3)) + name.substring(4);
                methods.put(prop, method);
                log.debug("Add row mapping for {} : {} setted by {}({})",
                        clazz.getName(), prop,
                        name, params[0].getType().getName());
            }
        }
    }

    @Override
    public T mapRow(ResultSet rs, int rowNum) throws SQLException {
        try {
            T bean = constructor.newInstance();
            var meta = rs.getMetaData();
            int cols = meta.getColumnCount();

            for (int i = 1; i <= cols; i++) {
                String prop = meta.getColumnLabel(i);
                Method method = methods.get(prop);

                if (method != null) {
                    method.invoke(bean, rs.getObject(i));
                } else {
                    Field field = fields.get(prop);
                    field.setAccessible(true);
                    field.set(bean, rs.getObject(i));
                }
            }

            return bean;
        } catch (ReflectiveOperationException e) {
            throw new DataAccessException(String.format("Cannot map result set to class %s", clazz.getName()));
        }
    }
}
