package ru.said.orm.next.core.table;

import ru.said.up.cache.core.Cache;
import ru.said.up.cache.core.CacheBuilder;
import ru.said.orm.next.core.field.DBField;
import ru.said.orm.next.core.field.ForeignCollectionField;
import ru.said.orm.next.core.field.field_type.*;
import ru.said.orm.next.core.table.utils.TableInfoUtils;
import ru.said.orm.next.core.table.validators.ForeignKeyValidator;
import ru.said.orm.next.core.table.validators.HasConstructorValidator;
import ru.said.orm.next.core.table.validators.IValidator;
import ru.said.orm.next.core.table.validators.PrimaryKeyValidator;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;

public final class TableInfo<T> {

    private Class<T> tableClass;

    private List<DBFieldType> dbFieldTypes;

    private List<ForeignFieldType> foreignFieldTypes;

    private List<ForeignCollectionFieldType> foreignCollectionFieldTypes;

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
                      List<DBFieldType> dbFieldTypes,
                      List<ForeignFieldType> foreignFieldTypes,
                      List<ForeignCollectionFieldType> foreignCollectionFieldTypes) {
        this.tableClass = tableClass;
        this.tableName = tableName;
        this.constructor = constructor;
        this.indexFieldTypes = indexFieldTypes;
        this.primaryKeyFieldType = primaryKeyFieldType;
        this.uniqueFieldTypes = uniqueFieldTypes;
        this.dbFieldTypes = dbFieldTypes;
        this.foreignFieldTypes = foreignFieldTypes;
        this.foreignCollectionFieldTypes = foreignCollectionFieldTypes;
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
        List<DBFieldType> dbFieldTypes = new ArrayList<>();
        List<ForeignFieldType> foreignFieldTypes = new ArrayList<>();
        List<ForeignCollectionFieldType> foreignCollectionFieldTypes = new ArrayList<>();

        //Не нравится этот код
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(DBField.class)) {
                DBField dbField = field.getAnnotation(DBField.class);
                if (dbField.foreign()) {
                    foreignFieldTypes.add(ForeignFieldType.ForeignFieldTypeCache.build(field));
                } else {
                    dbFieldTypes.add(DBFieldType.DBFieldTypeCache.build(field));
                }
            } else if (field.isAnnotationPresent(ForeignCollectionField.class)) {
                foreignCollectionFieldTypes.add(ForeignCollectionFieldType.ForeignCollectionFieldTypeCache.build(field));
            }
        }
        Optional<DBFieldType> primaryKeyFieldType = dbFieldTypes.stream()
                .filter(DBFieldType::isId)
                .findFirst();

        return new TableInfo<>(
                clazz,
                (Constructor<T>) lookupDefaultConstructor(clazz).get(),
                resolveUniques(clazz),
                resolveIndexes(clazz),
                primaryKeyFieldType.orElse(null),
                TableInfoUtils.resolveTableName(clazz),
                dbFieldTypes,
                foreignFieldTypes,
                foreignCollectionFieldTypes
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

    public Optional<DBFieldType> getDBFieldTypeByFieldName(String name) {
        return dbFieldTypes.stream().filter(dbFieldType -> dbFieldType.getField().getName().equals(name)).findFirst();
    }

    public Optional<ForeignFieldType> getForeignFieldTypeByFieldName(String name) {
        return foreignFieldTypes.stream().filter(foreignFieldType -> foreignFieldType.getField().getName().equals(name)).findFirst();
    }

    public Optional<ForeignCollectionFieldType> getForeignCollectionFieldTypeByFieldName(String name) {
        return foreignCollectionFieldTypes.stream().filter(foreignCollectionFieldType -> foreignCollectionFieldType.getField().getName().equals(name)).findFirst();
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


