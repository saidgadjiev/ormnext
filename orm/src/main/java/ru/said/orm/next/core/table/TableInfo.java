package ru.said.orm.next.core.table;

import ru.said.orm.next.core.field.field_type.*;
import ru.said.orm.next.core.table.utils.TableInfoUtils;
import ru.said.orm.next.core.table.validators.ForeignKeyValidator;
import ru.said.orm.next.core.table.validators.HasConstructorValidator;
import ru.said.orm.next.core.table.validators.IValidator;
import ru.said.orm.next.core.table.validators.PrimaryKeyValidator;
import ru.said.up.cache.core.Cache;
import ru.said.up.cache.core.CacheBuilder;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class TableInfo<T> {

    private Class<T> tableClass;

    private List<DBFieldType> dbFieldTypes;

    private List<ForeignFieldType> foreignFieldTypes;

    private List<ForeignCollectionFieldType> foreignCollectionFieldTypes;

    private List<UniqueFieldType> uniqueFieldTypes;

    private List<IndexFieldType> indexFieldTypes;

    private IDBFieldType primaryKeyFieldType;

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
                      String tableName,
                      List<IDBFieldType> fieldTypes) {
        this.tableClass = tableClass;
        this.tableName = tableName;
        this.constructor = constructor;
        this.indexFieldTypes = indexFieldTypes;
        this.primaryKeyFieldType = fieldTypes
                .stream()
                .filter(IDBFieldType::isId)
                .findAny()
                .orElse(null);
        this.uniqueFieldTypes = uniqueFieldTypes;
        this.dbFieldTypes = fieldTypes
                .stream()
                .filter(IDBFieldType::isDbFieldType)
                .map(idbFieldType -> (DBFieldType) idbFieldType)
                .collect(Collectors.toList());
        this.foreignFieldTypes = fieldTypes
                .stream()
                .filter(IDBFieldType::isForeignFieldType)
                .map(idbFieldType -> (ForeignFieldType) idbFieldType)
                .collect(Collectors.toList());
        this.foreignCollectionFieldTypes = fieldTypes
                .stream()
                .filter(IDBFieldType::isForeignCollectionFieldType)
                .map(idbFieldType -> (ForeignCollectionFieldType) idbFieldType)
                .collect(Collectors.toList());
    }

    public Class<T> getTableClass() {
        return tableClass;
    }

    public String getTableName() {
        return tableName;
    }

    public Optional<IDBFieldType> getPrimaryKey() {
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
        for (IValidator validator : validators) {
            validator.validate(clazz);
        }
        List<IDBFieldType> fieldTypes = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            FieldTypeUtils.create(field).ifPresent(fieldTypes::add);
        }
        String tableName = TableInfoUtils.resolveTableName(clazz);

        return new TableInfo<>(
                clazz,
                (Constructor<T>) lookupDefaultConstructor(clazz).get(),
                resolveUniques(fieldTypes, clazz),
                resolveIndexes(fieldTypes, tableName, clazz),
                TableInfoUtils.resolveTableName(clazz),
                fieldTypes
        );
    }

    private static <T> List<UniqueFieldType> resolveUniques(List<IDBFieldType> fieldTypes, Class<T> tClass) {
        List<UniqueFieldType> uniqueFieldTypes = new ArrayList<>();
        if (tClass.isAnnotationPresent(DBTable.class)) {
            Unique[] uniques = tClass.getAnnotation(DBTable.class).uniqueConstraints();

            for (Unique unique : uniques) {
                uniqueFieldTypes.add(new UniqueFieldType(validateUnique(fieldTypes, unique)));
            }
        }

        return uniqueFieldTypes;
    }

    private static <T> List<IndexFieldType> resolveIndexes(List<IDBFieldType> fieldTypes, String tableName, Class<T> tClass) {
        List<IndexFieldType> uniqueFieldTypes = new ArrayList<>();

        if (tClass.isAnnotationPresent(DBTable.class)) {
            Index[] indexes = tClass.getAnnotation(DBTable.class).indexes();

            for (Index index : indexes) {
                uniqueFieldTypes.add(new IndexFieldType(index.name(), index.unique(), tableName, validateIndex(fieldTypes, index)));
            }
        }

        return uniqueFieldTypes;
    }

    private static List<String> validateIndex(List<IDBFieldType> fieldTypes, Index index) {
        List<String> columns = new ArrayList<>();

        for (String columnName : index.columns()) {
            columns.add(fieldTypes
                    .stream()
                    .filter(idbFieldType -> {
                        if (idbFieldType.isForeignCollectionFieldType()) {
                            return false;
                        }
                        return idbFieldType.getField().getName().equals(columnName);
                    })
                    .findAny()
                    .orElseThrow(() -> new IllegalArgumentException("Indexed column [" + columnName + "] not annotated with DBField!"))
                    .getColumnName());
        }

        return columns;
    }

    private static List<String> validateUnique(List<IDBFieldType> fieldTypes, Unique unique) {
        List<String> columns = new ArrayList<>();

        for (String columnName : unique.columns()) {
            columns.add(fieldTypes
                    .stream()
                    .filter(idbFieldType -> {
                        if (idbFieldType.isForeignCollectionFieldType()) {
                            return false;
                        }

                        return idbFieldType.getField().getName().equals(columnName);
                    })
                    .findAny()
                    .orElseThrow(() -> new IllegalArgumentException("Indexed column [" + columnName + "] not annotated with DBField!"))
                    .getColumnName());
        }

        return columns;
    }

    private static Optional<Constructor<?>> lookupDefaultConstructor(Class<?> clazz) {
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


