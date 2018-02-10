package ru.saidgadjiev.orm.next.core.stament_executor.object;

import ru.saidgadjiev.orm.next.core.field.field_type.IDBFieldType;
import ru.saidgadjiev.orm.next.core.table.TableInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by said on 10.02.2018.
 */
public class ArgumentEjector {

    public static<T> Map<Integer, Object> eject(T object, TableInfo<T> tableInfo) throws Exception {
        AtomicInteger index = new AtomicInteger();
        Map<Integer, Object> args = new HashMap<>();

        for (IDBFieldType fieldType: tableInfo.getFieldTypes()) {
            if (!fieldType.isForeignCollectionFieldType() && !fieldType.isGenerated()) {
                Object value = fieldType.access(object);

                if (value != null) {
                    args.put(index.incrementAndGet(), value);
                } else {
                    args.put(index.incrementAndGet(), fieldType.getDefaultValue());
                }
            }
        }

        return args;
    }
}
