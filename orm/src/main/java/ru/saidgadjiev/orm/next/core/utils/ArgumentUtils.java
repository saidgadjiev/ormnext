package ru.saidgadjiev.orm.next.core.utils;

import ru.saidgadjiev.orm.next.core.field.field_type.ForeignColumnype;
import ru.saidgadjiev.orm.next.core.field.field_type.IDatabaseColumnType;
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

        for (IDatabaseColumnType fieldType : databaseEntityMetadata.toDBFieldTypes()) {
            if (!fieldType.isForeignCollectionFieldType() && !fieldType.isGenerated()) {
                Object value = fieldType.access(object);

                if (value != null) {
                    args.put(index.incrementAndGet(), fieldType.getDataPersister().parseJavaToSql(fieldType, value));
                } else {
                    args.put(index.incrementAndGet(), fieldType.getDefaultValue());
                }
            }
        }

        for (ForeignColumnype foreignColumnype : databaseEntityMetadata.toForeignFieldTypes()) {
            Object value = foreignColumnype.access(object);

            args.put(index.incrementAndGet(), foreignColumnype.getForeignPrimaryKey().access(value));
        }

        return args;
    }
}
