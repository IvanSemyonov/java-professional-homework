package ru.otus.jdbc.mapper;

import ru.otus.core.repository.DataTemplate;
import ru.otus.core.repository.DataTemplateException;
import ru.otus.core.repository.executor.DbExecutor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Сохратяет объект в базу, читает объект из базы
 */
public class DataTemplateJdbc<T> implements DataTemplate<T> {

    private final DbExecutor dbExecutor;
    private final EntitySQLMetaData entitySQLMetaData;
    private final EntityClassMetaData<T> entityClassMetaData;

    public DataTemplateJdbc(DbExecutor dbExecutor, EntitySQLMetaData entitySQLMetaData, EntityClassMetaData<T> entityClassMetaData) {
        this.dbExecutor = dbExecutor;
        this.entitySQLMetaData = entitySQLMetaData;
        this.entityClassMetaData = entityClassMetaData;
    }

    @Override
    public Optional<T> findById(Connection connection, long id) {
        String sql = entitySQLMetaData.getSelectByIdSql();
        return dbExecutor.executeSelect(connection, sql, List.of(id), resultSet -> {
            try {
                return resultSet.next() ? buildClient(resultSet) : null;
            } catch (SQLException e) {
                throw new DataTemplateException(e);
            }
        });
    }

    @Override
    public List<T> findAll(Connection connection) {
        String sql = entitySQLMetaData.getSelectAllSql();
        return dbExecutor.executeSelect(connection, sql, Collections.emptyList(), resultSet -> {
            try {
                List<T> objects = new ArrayList<>();
                while (resultSet.next()) {
                    objects.add(buildClient(resultSet));
                }
                return objects;
            } catch (SQLException e) {
                throw new DataTemplateException(e);
            }
        }).orElse(Collections.emptyList());
    }

    @Override
    public long insert(Connection connection, T client) {
        String sql = entitySQLMetaData.getInsertSql();
        return dbExecutor.executeStatement(connection, sql, getFieldValues(client));
    }

    @Override
    public void update(Connection connection, T client) {
        String sql = entitySQLMetaData.getUpdateSql();
        List<Object> fieldValues = getFieldValues(client);
        fieldValues.add(getFieldValue(entityClassMetaData.getIdField(), client));
        dbExecutor.executeStatement(connection, sql, fieldValues);
    }

    private List<Object> getFieldValues(T client) {
        return entityClassMetaData.getFieldsWithoutId().stream()
                .map(field -> getFieldValue(field, client))
                .collect(Collectors.toList());
    }

    private Object getFieldValue(Field field, T client) {
        try {
            field.setAccessible(true);
            return field.get(client);
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    private T buildClient(ResultSet resultSet) {
        try {
            Constructor<T> constructor = entityClassMetaData.getConstructor();
            T client = constructor.newInstance();
            for (Field field : entityClassMetaData.getAllFields()) {
                setField(client, field, resultSet);
            }
            return client;
        } catch (ReflectiveOperationException e) {
            throw new DataTemplateException(e);
        }
    }

    private void setField(T client, Field field, ResultSet resultSet) {
        try {
            Object value = resultSet.getObject(field.getName());
            field.setAccessible(true);
            field.set(client, value);
        } catch (SQLException e) {
        } catch (IllegalAccessException e) {
            throw new DataTemplateException(e);
        }
    }
}
