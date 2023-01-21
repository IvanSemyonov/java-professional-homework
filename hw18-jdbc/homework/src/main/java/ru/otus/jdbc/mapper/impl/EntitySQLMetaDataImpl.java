package ru.otus.jdbc.mapper.impl;

import ru.otus.jdbc.mapper.EntityClassMetaData;
import ru.otus.jdbc.mapper.EntitySQLMetaData;

import java.lang.reflect.Field;
import java.util.StringJoiner;


public class EntitySQLMetaDataImpl<T> implements EntitySQLMetaData {

    private final EntityClassMetaData<T> entityClassMetaData;

    public EntitySQLMetaDataImpl(EntityClassMetaData<T> entityClassMetaData) {
        this.entityClassMetaData = entityClassMetaData;
    }

    @Override
    public String getSelectAllSql() {
        return "select * from " + entityClassMetaData.getName();
    }

    @Override
    public String getSelectByIdSql() {
        String idFieldName = entityClassMetaData.getIdField().getName();
        return "select * from " + entityClassMetaData.getName() + " where " + idFieldName + " = ?";
    }

    @Override
    public String getInsertSql() {
        StringJoiner columns = new StringJoiner(",", "(", ")");
        StringJoiner values = new StringJoiner(",", "(", ")");

        for (Field field : entityClassMetaData.getFieldsWithoutId()) {
            columns.add(field.getName());
            values.add("?");
        }

        return "insert into " + entityClassMetaData.getName() + " " + columns + " values" + values;
    }

    @Override
    public String getUpdateSql() {
        StringJoiner columns = new StringJoiner(",");

        for (Field field : entityClassMetaData.getFieldsWithoutId()) {
            columns.add(field.getName() + " = ?");
        }

        String idFieldName = entityClassMetaData.getIdField().getName();

        return "update " + entityClassMetaData.getName() + " set " + columns
                + " where " + idFieldName + " = ?";
    }
}
