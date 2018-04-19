package ru.saidgadjiev.orm.next.core.utils;

import ru.saidgadjiev.orm.next.core.field.fieldtype.ForeignColumnType;
import ru.saidgadjiev.orm.next.core.field.fieldtype.IDatabaseColumnType;
import ru.saidgadjiev.orm.next.core.table.DatabaseEntityMetadata;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by said on 10.02.2018.
 */
public class ArgumentUtils {

    public static <T> Map<Integer, Object> eject(T object, DatabaseEntityMetadata<T> databaseEntityMetadata) throws Exception {
        AtomicInteger index = new AtomicInteger();
        Map<Integer, Object> args = new HashMap<>();

        for (IDatabaseColumnType fieldType : databaseEntityMetadata.getFieldTypes()) {
            if (!fieldType.isForeignCollectionFieldType() && !fieldType.isGenerated() && !fieldType.isForeignFieldType()) {
                Object value = fieldType.access(object);

                if (value != null) {
                    args.put(index.incrementAndGet(), fieldType.getDataPersister().parseJavaToSql(fieldType, value));
                } else {
                    args.put(index.incrementAndGet(), fieldType.getDefaultValue());
                }
            }
        }

        for (ForeignColumnType foreignColumnType : databaseEntityMetadata.toForeignFieldTypes()) {
            Object value = foreignColumnType.access(object);

            args.put(index.incrementAndGet(), foreignColumnType.getForeignPrimaryKey().access(value));
        }

        return args;
    }
}
