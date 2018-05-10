package ru.saidgadjiev.orm.next.core.utils;

import ru.saidgadjiev.orm.next.core.field.fieldtype.ForeignColumnType;
import ru.saidgadjiev.orm.next.core.field.fieldtype.IDatabaseColumnType;
import ru.saidgadjiev.orm.next.core.table.internal.metamodel.DatabaseEntityMetadata;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by said on 10.02.2018.
 */
public class ArgumentUtils {

    public static List<Object> eject(Object object, DatabaseEntityMetadata<?> databaseEntityMetadata) throws Exception {
        List<Object> args = new ArrayList<>();

        for (IDatabaseColumnType fieldType : databaseEntityMetadata.getFieldTypes()) {
            if (!fieldType.isForeignCollectionFieldType() && !fieldType.isGenerated() && !fieldType.isForeignFieldType()) {
                Object value = fieldType.access(object);

                if (value != null) {
                    args.add(fieldType.getDataPersister().parseJavaToSql(fieldType, value));
                } else {
                    args.add(fieldType.getDefaultValue());
                }
            }
        }

        for (ForeignColumnType foreignColumnType : databaseEntityMetadata.toForeignFieldTypes()) {
            Object value = foreignColumnType.access(object);

            args.add(foreignColumnType.getForeignPrimaryKey().access(value));
        }

        return args;
    }
}
