package ru.saidgadjiev.ormnext.core.utils;

import ru.saidgadjiev.ormnext.core.field.DatabaseColumn;
import ru.saidgadjiev.ormnext.core.field.fieldtype.SimpleDatabaseColumnTypeImpl;
import ru.saidgadjiev.ormnext.core.field.fieldtype.DatabaseColumnType;
import ru.saidgadjiev.ormnext.core.table.DatabaseEntity;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Entity metadata utils.
 *
 * @author Said Gadjiev
 */
public final class DatabaseEntityMetadataUtils {

    /**
     * Cached table names.
     */
    private static final Map<Class<?>, String> RESOLVED_TABLE_NAMES = new HashMap<>();

    /**
     * Utils class can't be instantiated.
     */
    private DatabaseEntityMetadataUtils() { }

    /**
     * Resolve entity primary key.
     * @param entityClass entity class
     * @return optional primary key
     */
    public static Optional<DatabaseColumnType> resolvePrimaryKey(Class<?> entityClass) {
        for (Field field : entityClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(DatabaseColumn.class) && field.getAnnotation(DatabaseColumn.class).id()) {
                return Optional.ofNullable(SimpleDatabaseColumnTypeImpl.build(field));
            }
        }

        return Optional.empty();
    }

    /**
     * Resolve table name by entity class.
     * @param entityClass entity class
     * @return table name
     */
    public static String resolveTableName(Class<?> entityClass) {
        if (RESOLVED_TABLE_NAMES.containsKey(entityClass)) {
            return RESOLVED_TABLE_NAMES.get(entityClass);
        }
        String tableName = "";

        if (entityClass.isAnnotationPresent(DatabaseEntity.class)) {
            DatabaseEntity databaseEntity = entityClass.getAnnotation(DatabaseEntity.class);

            tableName = databaseEntity.name();
        }

        tableName = tableName.isEmpty() ? entityClass.getSimpleName().toLowerCase() : tableName;
        RESOLVED_TABLE_NAMES.put(entityClass, tableName);

        return tableName;
    }

    /**
     * Find column name by property name in requested column types.
     * @param columnTypes target column types
     * @param propertyName property name
     * @return optional column name
     */
    public static Optional<String> getColumnNameByPropertyName(List<DatabaseColumnType> columnTypes,
                                                               String propertyName) {
        for (DatabaseColumnType columnType : columnTypes) {
            if (columnType.foreignCollectionColumnType()) {
                continue;
            }

            if (columnType.getField().getName().equals(propertyName)) {
                return Optional.ofNullable(columnType.columnName());
            }
        }

        return Optional.empty();
    }

    /**
     * Find column type by property name in requested column types.
     * @param columnTypes target column types
     * @param propertyName target property name
     * @return optional column type
     */
    public static Optional<DatabaseColumnType> getDataTypeByPropertyName(List<DatabaseColumnType> columnTypes,
                                                                         String propertyName) {
        for (DatabaseColumnType columnType : columnTypes) {
            if (columnType.foreignCollectionColumnType()) {
                continue;
            }

            if (columnType.getField().getName().equals(propertyName)) {
                return Optional.of(columnType);
            }
        }

        return Optional.empty();
    }

}
