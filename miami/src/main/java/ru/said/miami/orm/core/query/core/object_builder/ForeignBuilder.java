package ru.said.miami.orm.core.query.core.object_builder;

import ru.said.miami.orm.core.field.DBFieldType;
import ru.said.miami.orm.core.query.core.IMiamiData;
import ru.said.miami.orm.core.table.TableInfo;

/**
 * Created by said on 14.10.17.
 */
public class ForeignBuilder extends ObjectPartBuilder {

    private TableInfo<?> tableInfo;

    public ForeignBuilder(TableInfo<?> tableInfo) {
        this.tableInfo = tableInfo;
    }

    @Override
    public boolean check(IMiamiData data, Object object) throws Exception {
        for (DBFieldType fieldType : tableInfo.toDBFieldTypes()) {
            if (fieldType.isForeign()) {
                TableInfo<?> foreignTableInfo = TableInfo.buildTableInfo(fieldType.getForeignFieldType());
                Object val = foreignTableInfo.getConstructor().newInstance();

                if (foreignTableInfo.getIdField().isPresent()) {
                    foreignTableInfo.getIdField().get().assignField(val, data.getObject(fieldType.getFieldName()));
                }
                fieldType.assignField(object, val);
            }
        }

        return checkNext(data, object);
    }
}
