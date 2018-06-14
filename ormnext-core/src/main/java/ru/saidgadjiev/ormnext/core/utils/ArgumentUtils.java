package ru.saidgadjiev.ormnext.core.utils;

import ru.saidgadjiev.ormnext.core.field.datapersister.ColumnConverter;
import ru.saidgadjiev.ormnext.core.field.fieldtype.DatabaseColumnType;
import ru.saidgadjiev.ormnext.core.field.fieldtype.ForeignColumnTypeImpl;
import ru.saidgadjiev.ormnext.core.loader.Argument;
import ru.saidgadjiev.ormnext.core.table.internal.metamodel.DatabaseEntityMetadata;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Util class for operation with arguments.
 *
 * @author Said Gadjiev
 */
public final class ArgumentUtils {

    /**
     * Can't be instantiated.
     */
    private ArgumentUtils() {
    }

    /**
     * Retrieve arguments with sql values from object {@code object} for insert statement.
     *
     * @param object                 target object.
     * @param databaseEntityMetadata target object entity metadata
     * @return retrieved arguments
     * @throws SQLException any exceptions
     */
    public static Map<DatabaseColumnType, Argument> ejectForCreate(Object object,
                                                                   DatabaseEntityMetadata<?> databaseEntityMetadata
    ) throws SQLException {
        Map<DatabaseColumnType, Argument> args = new LinkedHashMap<>();

        for (DatabaseColumnType columnType : databaseEntityMetadata.getColumnTypes()) {
            if (columnType.foreignCollectionColumnType()) {
                continue;
            }
            Object value = columnType.access(object);

            if (columnType.databaseColumnType()) {
                if (!columnType.generated() && columnType.insertable()) {
                    if (value != null) {
                        args.put(columnType, processConvertersToSqlValue(
                                value,
                                columnType
                        ));
                    } else if (!columnType.defaultIfNull()) {
                        args.put(columnType, new Argument(columnType.dataPersister(), null));
                    }
                }
            } else {
                ForeignColumnTypeImpl foreignColumnType = ((ForeignColumnTypeImpl) columnType);
                DatabaseColumnType foreignDatabaseColumnType = foreignColumnType.getForeignDatabaseColumnType();

                args.put(columnType, eject(value, foreignDatabaseColumnType));
            }
        }

        return args;
    }

    /**
     * Retrieve arguments with sql values from object {@code object} for update statement.
     *
     * @param object                 target object.
     * @param databaseEntityMetadata target object entity metadata
     * @return retrieved arguments
     * @throws SQLException any exceptions
     */
    public static Map<DatabaseColumnType, Argument> ejectForUpdate(Object object,
                                                                   DatabaseEntityMetadata<?> databaseEntityMetadata
    ) throws SQLException {
        Map<DatabaseColumnType, Argument> args = new LinkedHashMap<>();

        for (DatabaseColumnType columnType : databaseEntityMetadata.getColumnTypes()) {
            if (columnType.foreignCollectionColumnType()) {
                continue;
            }
            Object value = columnType.access(object);

            if (columnType.databaseColumnType()) {
                if (!columnType.id() && columnType.updatable()) {
                    if (value != null) {
                        args.put(columnType, processConvertersToSqlValue(
                                value,
                                columnType
                        ));
                    } else if (!columnType.defaultIfNull()) {
                        args.put(columnType, new Argument(columnType.dataPersister(), null));
                    }
                }
            } else {
                ForeignColumnTypeImpl foreignColumnType = ((ForeignColumnTypeImpl) columnType);
                DatabaseColumnType foreignDatabaseColumnType = foreignColumnType.getForeignDatabaseColumnType();

                args.put(columnType, eject(value, foreignDatabaseColumnType));
            }
        }

        return args;
    }

    /**
     * Retrieve argument from requested column type in object.
     *
     * @param object             target object
     * @param databaseColumnType target column type
     * @return argument
     * @throws SQLException any exceptions
     */
    public static Argument eject(Object object, DatabaseColumnType databaseColumnType) throws SQLException {
        if (object == null) {
            return new Argument(databaseColumnType.dataPersister(), null);
        }
        return processConvertersToSqlValue(
                databaseColumnType.access(object),
                databaseColumnType
        );
    }

    /**
     * Convert java value to SQL with converters in requested column type
     * {@link DatabaseColumnType#getColumnConverters()}.
     *
     * @param javaValue          target java value
     * @param databaseColumnType column type
     * @return argument with sql value
     * @throws SQLException any exceptions
     */
    public static Argument processConvertersToSqlValue(Object javaValue, DatabaseColumnType databaseColumnType)
            throws SQLException {
        if (javaValue == null) {
            return new Argument(databaseColumnType.dataPersister(), null);
        }
        Object result = javaValue;

        if (databaseColumnType.getColumnConverters().isPresent()) {
            for (ColumnConverter columnConverter : databaseColumnType.getColumnConverters().get()) {
                result = columnConverter.javaToSql(result);
            }
        }

        return new Argument(databaseColumnType.dataPersister(), result);
    }

    /**
     * Convert java value to SQL with converters in requested column type
     * {@link DatabaseColumnType#getColumnConverters()}.
     *
     * @param sqlValue          target sql value
     * @param databaseColumnType column type
     * @return argument with sql value
     * @throws SQLException any exceptions
     */
    public static Argument processConvertersToJavaValue(Object sqlValue, DatabaseColumnType databaseColumnType)
            throws SQLException {
        if (sqlValue == null) {
            return new Argument(databaseColumnType.dataPersister(), null);
        }
        Object result = sqlValue;

        if (databaseColumnType.getColumnConverters().isPresent()) {
            for (ColumnConverter columnConverter : databaseColumnType.getColumnConverters().get()) {
                result = columnConverter.sqlToJava(result);
            }
        }

        return new Argument(databaseColumnType.dataPersister(), result);
    }
}
