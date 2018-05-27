package ru.saidgadjiev.ormnext.core.utils;

import ru.saidgadjiev.ormnext.core.field.field_type.ForeignColumnType;
import ru.saidgadjiev.ormnext.core.field.field_type.IDatabaseColumnType;
import ru.saidgadjiev.ormnext.core.field.persister.ColumnConverter;
import ru.saidgadjiev.ormnext.core.stament_executor.Argument;
import ru.saidgadjiev.ormnext.core.table.internal.metamodel.DatabaseEntityMetadata;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Util class for operation with arguments.
 */
public final class ArgumentUtils {

    /**
     * Can't be instantiated.
     */
    private ArgumentUtils() { }

    /**
     * Retrieve arguments with sql values from object {@code object}.
     * If value has default definition and it is null and if it not insertable it will be ignored.
     * @param object target object.
     * @param databaseEntityMetadata target object entity metadata
     * @return retrieved arguments
     * @throws SQLException any exceptions
     */
    public static Map<IDatabaseColumnType, Argument> eject(Object object,
                                                           DatabaseEntityMetadata<?> databaseEntityMetadata
    ) throws SQLException {
        Map<IDatabaseColumnType, Argument> args = new LinkedHashMap<>();

        for (IDatabaseColumnType columnType : databaseEntityMetadata.toDatabaseColumnTypes()) {
            if (!columnType.isGenerated() && columnType.insertable()) {
                Object value = columnType.access(object);

                if (value != null || columnType.getDefaultDefinition() == null) {
                    args.put(columnType, eject(object, columnType));
                }
            }
        }

        for (ForeignColumnType foreignColumnType : databaseEntityMetadata.toForeignColumnTypes()) {
            Object value = foreignColumnType.access(object);
            IDatabaseColumnType foreignPrimaryKeyType = foreignColumnType.getForeignPrimaryKey();
            Object foreignObject = foreignPrimaryKeyType.access(value);

            args.put(foreignColumnType, eject(foreignObject, foreignColumnType.getForeignPrimaryKey()));
        }

        return args;
    }

    /**
     * Retrieve argument from requested column type in object.
     * @param object target object
     * @param databaseColumnType target column type
     * @return argument
     * @throws SQLException any exceptions
     */
    public static Argument eject(Object object, IDatabaseColumnType databaseColumnType) throws SQLException {
        return processConvertersToSqlValue(
                databaseColumnType.access(object),
                databaseColumnType
        );
    }

    /**
     * Convert java value to SQL with converters in requested column type
     * {@link IDatabaseColumnType#getColumnConverters()}.
     *
     * @param javaValue target java value
     * @param databaseColumnType column type
     * @return argument with sql value
     * @throws SQLException any exceptions
     */
    public static Argument processConvertersToSqlValue(Object javaValue, IDatabaseColumnType databaseColumnType)
            throws SQLException {
        Object result = javaValue;

        if (databaseColumnType.getColumnConverters().isPresent()) {
            for (ColumnConverter columnConverter : databaseColumnType.getColumnConverters().get()) {
                result = columnConverter.javaToSql(result);
            }
        }

        return new Argument(databaseColumnType.getDataType(), result);
    }
}
