package ru.said.miami.orm.core.table;

import ru.said.miami.cache.core.Cache;
import ru.said.miami.cache.core.CacheBuilder;
import ru.said.miami.orm.core.field.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;

public final class TableInfo<T> {

    private final List<FieldType> fieldTypes;

    private List<DBFieldType> dbFieldTypes;

    private List<ForeignFieldType> foreignFieldTypes;

    private List<ForeignCollectionFieldType> foreignCollectionFieldTypes;

    private List<UniqueFieldType> uniqueFieldTypes;

    private PrimaryKeyFieldType primaryKeyFieldType;

    private String tableName;

    private Constructor<T> constructor;

    private TableInfo(Constructor<T> constructor,
                      List<UniqueFieldType> uniqueFieldTypes,
                      PrimaryKeyFieldType primaryKeyFieldType,
                      String tableName,
                      List<FieldType> fieldTypes) {
        this.tableName = tableName;
        this.constructor = constructor;
        this.fieldTypes = fieldTypes;
        this.primaryKeyFieldType = primaryKeyFieldType;
        this.uniqueFieldTypes = uniqueFieldTypes;

        fieldTypes.forEach(fieldType -> {
            if (fieldType.isDBFieldType()) {
                dbFieldTypes.add(fieldType.getDbFieldType());
            } else if (fieldType.isForeignCollectionFieldType()) {
                foreignCollectionFieldTypes.add(fieldType.getForeignCollectionFieldType());
            } else {
                foreignFieldTypes.add(fieldType.getForeignField());
            }
        });
    }

    public String getTableName() {
        return tableName;
    }

    public Optional<PrimaryKeyFieldType> getIdField() {
        return Optional.ofNullable(primaryKeyFieldType);
    }

    public List<UniqueFieldType> getUniqueFieldTypes() {
        return uniqueFieldTypes;
    }

    public Constructor<T> getConstructor() {
        return constructor;
    }

    public List<DBFieldType> toDBFieldTypes() {
        return Collections.unmodifiableList(dbFieldTypes);
    }

    public List<ForeignFieldType> toForeignFieldTypes() {
        return Collections.unmodifiableList(foreignFieldTypes);
    }

    public List<ForeignCollectionFieldType> toForeignCollectionFieldTypes() {
        return Collections.unmodifiableList(foreignCollectionFieldTypes);
    }

    public static<T> TableInfo<T> build(Class<T> clazz) throws NoSuchMethodException, NoSuchFieldException {
        List<FieldType> fieldTypes = new ArrayList<>();

        for (Field field: clazz.getDeclaredFields()) {
            FieldType.buildFieldType(field).ifPresent(fieldTypes::add);
        }
        if (fieldTypes.isEmpty()) {
            throw new IllegalArgumentException("No fields have a " + DBField.class.getSimpleName()
                    + " annotation in " + clazz);
        }
        String tableName = "";

        if (clazz.isAnnotationPresent(DBTable.class)) {
            DBTable dbTable = clazz.getAnnotation(DBTable.class);

            tableName = dbTable.name();
        }
        TableInfo<T> tableInfo = new TableInfo<>(
                (Constructor<T>) lookupDefaultConstructor(clazz),
                getUniques(clazz),
                getPrimaryKeys(clazz),
                tableName.isEmpty() ? clazz.getSimpleName().toLowerCase() : tableName,
                fieldTypes
        );

        return tableInfo;
    }

    private static<T> List<UniqueFieldType> getUniques(Class<T> tClass) throws NoSuchFieldException, NoSuchMethodException {
        List<UniqueFieldType> uniqueFieldTypes = new ArrayList<>();
        Unique[] uniques = tClass.getAnnotation(DBTable.class).uniqueConstraints();

        for (Unique unique: uniques) {
            uniqueFieldTypes.add(UniqueFieldType.build(unique));
        }

        return uniqueFieldTypes;
    }

    private static<T> PrimaryKeyFieldType getPrimaryKeys(Class<T> tClass) throws NoSuchFieldException, NoSuchMethodException {
        PrimaryKey primaryKey = tClass.getAnnotation(DBTable.class).primaryKey();

        return PrimaryKeyFieldType.build(primaryKey.column(), tClass);
    }

    private static Constructor<?> lookupDefaultConstructor(Class<?> clazz) throws NoSuchMethodException {
        for (Constructor<?> constructor: clazz.getDeclaredConstructors()) {
            if (constructor.getParameterCount() == 0) {
                return constructor;
            }
        }
        throw new IllegalArgumentException("No define default constructor");
    }

    public static class TableInfoCache {

        private static final Cache<Class<?>, TableInfo<?>> CACHE = CacheBuilder.newRefenceCacheBuilder().build();

        public static<T> TableInfo<T> build(Class<T> clazz) throws NoSuchMethodException, NoSuchFieldException {
            if (CACHE.contains(clazz)) {
                return (TableInfo<T>) CACHE.get(clazz);
            }
            TableInfo<?> tableInfo = TableInfo.build(clazz);

            CACHE.put(clazz, tableInfo);

            return (TableInfo<T>) tableInfo;
        }
    }
}


