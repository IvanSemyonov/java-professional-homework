package ru.otus.orm.mapper.impl;

import ru.otus.orm.core.annotations.Id;
import ru.otus.orm.mapper.EntityClassMetaData;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class EntityClassMetaDataImpl<T> implements EntityClassMetaData<T> {

    private final Constructor<T> constructor;
    private final Field idField;
    private final List<Field> allFields;
    private final List<Field> fieldsWithoutId;

    private final String name;

    public EntityClassMetaDataImpl(Class<T> persistentClass) {
        try {
            this.constructor = persistentClass.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        this.name = persistentClass.getSimpleName().toLowerCase();
        this.allFields = Arrays.asList(persistentClass.getDeclaredFields());
        this.idField = this.allFields.stream()
                .filter(field -> field.isAnnotationPresent(Id.class))
                .findFirst()
                .orElseThrow();
        this.fieldsWithoutId = this.allFields.stream()
                .filter(Predicate.not(this.idField::equals))
                .toList();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Constructor<T> getConstructor() {
        return constructor;
    }

    @Override
    public Field getIdField() {
        return idField;
    }

    @Override
    public List<Field> getAllFields() {
        return allFields;
    }

    @Override
    public List<Field> getFieldsWithoutId() {
        return fieldsWithoutId;
    }
}
