package ru.said.miami.orm.core.query.core.object_builder;

import ru.said.miami.orm.core.field.DBFieldType;
import ru.said.miami.orm.core.query.core.IMiamiData;
import ru.said.miami.orm.core.table.TableInfo;

/**
 * Created by said on 14.10.17.
 */
public class BaseBuilder extends ObjectPartBuilder {

    private TableInfo<?> tableInfo;

    public BaseBuilder(TableInfo<?> tableInfo) {
        this.tableInfo = tableInfo;
    }

    @Override
    public boolean check(IMiamiData data, Object object) throws Exception {
        for (DBFieldType fieldType : tableInfo.toDBFieldTypes()) {
            if (!fieldType.isForeign()) {
                fieldType.assignField(object, data.getObject(fieldType.getFieldName()));
            }
        }

        return checkNext(data, object);
    }
}
