package ru.otus.orm.mapper.impl;

import ru.otus.orm.mapper.EntityClassMetaData;
import ru.otus.orm.mapper.EntitySQLMetaData;

import java.lang.reflect.Field;
import java.util.List;
import java.util.StringJoiner;


public class EntitySQLMetaDataImpl<T> implements EntitySQLMetaData {

    private final String selectAllQuery;
    private final String selectByIdQuery;
    private final String insertQuery;
    private final String updateQuery;



    public EntitySQLMetaDataImpl(EntityClassMetaData<T> entityClassMetaData) {
        String idFieldName = entityClassMetaData.getIdField().getName();
        String tableName = entityClassMetaData.getName();
        List<Field> fieldsWithoutId = entityClassMetaData.getFieldsWithoutId();
        this.selectAllQuery = "select * from " + tableName;
        this.selectByIdQuery = "select * from " + tableName + " where " + idFieldName + " = ?";
        this.insertQuery = buildInsertQuery(tableName, fieldsWithoutId);
        this.updateQuery = buildUpdateQuery(tableName, idFieldName, fieldsWithoutId);
    }

    private String buildInsertQuery(String tableName, List<Field> fieldsWithoutId) {
        StringJoiner columns = new StringJoiner(",", "(", ")");
        StringJoiner values = new StringJoiner(",", "(", ")");

        for (Field field : fieldsWithoutId) {
            columns.add(field.getName());
            values.add("?");
        }

        return "insert into " + tableName + " " + columns + " values" + values;
    }

    private String buildUpdateQuery(String tableName, String idFieldName, List<Field> fieldsWithoutId) {
        StringJoiner columns = new StringJoiner(",");
        for (Field field : fieldsWithoutId) {
            columns.add(field.getName() + " = ?");
        }
        return "update " + tableName + " set " + columns + " where " + idFieldName + " = ?";
    }

    @Override
    public String getSelectAllSql() {
        return selectAllQuery;
    }

    @Override
    public String getSelectByIdSql() {
        return selectByIdQuery;
    }

    @Override
    public String getInsertSql() {
        return insertQuery;
    }

    @Override
    public String getUpdateSql() {
        return updateQuery;
    }
}
