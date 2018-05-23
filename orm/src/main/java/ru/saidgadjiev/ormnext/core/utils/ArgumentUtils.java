package ru.saidgadjiev.ormnext.core.utils;

import ru.saidgadjiev.ormnext.core.field.fieldtype.ForeignColumnType;
import ru.saidgadjiev.ormnext.core.field.fieldtype.IDatabaseColumnType;
import ru.saidgadjiev.ormnext.core.stamentexecutor.Argument;
import ru.saidgadjiev.ormnext.core.table.internal.metamodel.DatabaseEntityMetadata;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by said on 10.02.2018.
 */
public class ArgumentUtils {

    public static Map<IDatabaseColumnType, Argument> eject(Object object, DatabaseEntityMetadata<?> databaseEntityMetadata) throws Exception {
        Map<IDatabaseColumnType, Argument> args = new LinkedHashMap<>();

        for (IDatabaseColumnType fieldType : databaseEntityMetadata.toDBFieldTypes()) {
            if (!fieldType.isGenerated() && fieldType.insertable()) {
                Object value = fieldType.access(object);

                if (value != null || fieldType.getDefaultDefinition() == null) {
                    args.put(fieldType, Argument.from(fieldType, value));
                }
            }
        }

        for (ForeignColumnType foreignColumnType : databaseEntityMetadata.toForeignFieldTypes()) {
            Object value = foreignColumnType.access(object);
            Object foreignPrimaryKey = foreignColumnType.getForeignPrimaryKey().access(value);

            args.put(foreignColumnType, Argument.from(foreignColumnType, foreignPrimaryKey));
        }

        return args;
    }

}
