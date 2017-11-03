package ru.said.miami.orm.core.table;

import ru.said.miami.cache.core.Cache;
import ru.said.miami.cache.core.CacheBuilder;
import ru.said.miami.orm.core.field.*;
import ru.said.miami.orm.core.table.validator.ForeignKeyValidator;
import ru.said.miami.orm.core.table.validator.HasConstructorValidator;
import ru.said.miami.orm.core.table.validator.IValidator;
import ru.said.miami.orm.core.table.validator.PrimaryKeyValidator;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;

public final class TableInfo<T> {

    private Class<T> tableClass;

    private List<DBFieldType> dbFieldTypes = new ArrayList<>();

    private List<ForeignFieldType> foreignFieldTypes = new ArrayList<>();

    private List<ForeignCollectionFieldType> foreignCollectionFieldTypes = new ArrayList<>();

    private List<UniqueFieldType> uniqueFieldTypes;

    private List<IndexFieldType> indexFieldTypes;

    private DBFieldType primaryKeyFieldType;

    private String tableName;

    private Constructor<T> constructor;

    private static List<IValidator> validators = new ArrayList<IValidator>() {{
        add(new ForeignKeyValidator());
        add(new HasConstructorValidator());
        add(new PrimaryKeyValidator());
    }};

    private TableInfo(Class<T> tableClass,
                      Constructor<T> constructor,
                      List<UniqueFieldType> uniqueFieldTypes,
                      List<IndexFieldType> indexFieldTypes,
                      DBFieldType primaryKeyFieldType,
                      String tableName,
                      List<FieldType> fieldTypes) {
        this.tableClass = tableClass;
        this.tableName = tableName;
        this.constructor = constructor;
        this.indexFieldTypes = indexFieldTypes;
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

    public Class<T> getTableClass() {
        return tableClass;
    }

    public String getTableName() {
        return tableName;
    }

    public Optional<DBFieldType> getPrimaryKeys() {
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

    public List<IndexFieldType> getIndexFieldTypes() {
        return indexFieldTypes;
    }

    public static <T> TableInfo<T> build(Class<T> clazz) throws Exception {
        for (IValidator validator: validators) {
            validator.validate(clazz);
        }
        List<FieldType> fieldTypes = new ArrayList<>();

        for (Field field : clazz.getDeclaredFields()) {
            FieldType.buildFieldType(field).ifPresent(fieldTypes::add);
        }
        if (fieldTypes.isEmpty()) {
            throw new IllegalArgumentException("No fields have a " + DBField.class.getSimpleName()
                    + " annotation in " + clazz);
        }
        Optional<FieldType> primaryKeyFieldType = fieldTypes.stream()
                .filter(fieldType -> fieldType.isDBFieldType() && fieldType.getDbFieldType().isId())
                .findFirst();

        return new TableInfo<>(
                clazz,
                (Constructor<T>) lookupDefaultConstructor(clazz).get(),
                resolveUniques(clazz),
                resolveIndexes(clazz),
                primaryKeyFieldType.map(FieldType::getDbFieldType).orElse(null),
                TableInfoUtils.resolveTableName(clazz),
                fieldTypes
        );
    }

    private static <T> List<UniqueFieldType> resolveUniques(Class<T> tClass) throws NoSuchFieldException, NoSuchMethodException {
        List<UniqueFieldType> uniqueFieldTypes = new ArrayList<>();
        if (tClass.isAnnotationPresent(DBTable.class)) {
            Unique[] uniques = tClass.getAnnotation(DBTable.class).uniqueConstraints();

            for (Unique unique : uniques) {
                uniqueFieldTypes.add(UniqueFieldType.build(unique, tClass));
            }
        }

        return uniqueFieldTypes;
    }

    private static <T> List<IndexFieldType> resolveIndexes(Class<T> tClass) throws NoSuchFieldException, NoSuchMethodException {
        List<IndexFieldType> uniqueFieldTypes = new ArrayList<>();
        if (tClass.isAnnotationPresent(DBTable.class)) {
            Index[] indexes = tClass.getAnnotation(DBTable.class).indexes();

            for (Index index : indexes) {
                uniqueFieldTypes.add(IndexFieldType.build(index, tClass));
            }
        }

        return uniqueFieldTypes;
    }

    private static Optional<Constructor<?>> lookupDefaultConstructor(Class<?> clazz) throws NoSuchMethodException {
        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            if (constructor.getParameterCount() == 0) {
                return Optional.of(constructor);
            }
        }

        return Optional.empty();
    }

    public static class TableInfoCache {

        private static final Cache<Class<?>, TableInfo<?>> CACHE = CacheBuilder.newRefenceCacheBuilder().build();

        public static <T> TableInfo<T> build(Class<T> clazz) throws Exception {
            if (CACHE.contains(clazz)) {
                return (TableInfo<T>) CACHE.get(clazz);
            }
            TableInfo<?> tableInfo = TableInfo.build(clazz);

            CACHE.put(clazz, tableInfo);

            return (TableInfo<T>) tableInfo;
        }
    }
}


