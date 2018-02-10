package ru.saidgadjiev.orm.next.core.stament_executor.object;

import ru.saidgadjiev.orm.next.core.field.field_type.DBFieldType;
import ru.saidgadjiev.orm.next.core.field.field_type.ForeignFieldType;
import ru.saidgadjiev.orm.next.core.query.core.CreateQuery;
import ru.saidgadjiev.orm.next.core.query.core.common.UpdateValue;
import ru.saidgadjiev.orm.next.core.query.core.literals.Param;
import ru.saidgadjiev.orm.next.core.table.TableInfo;

public class CreateQueryBuilder<T> {

    private TableInfo<T> tableInfo;

    private CreateQuery query;

    public CreateQueryBuilder(TableInfo<T> tableInfo) {
        this.tableInfo = tableInfo;
    }

    public CreateQueryBuilder newObject() throws Exception {
        this.query = new CreateQuery(tableInfo.getTableName());

        return this;
    }

    public CreateQueryBuilder<T> createBase() throws Exception {
        for (DBFieldType fieldType : tableInfo.toDBFieldTypes()) {
            if (fieldType.isId() && fieldType.isGenerated()) {
                continue;
            }
            query.add(new UpdateValue(
                    fieldType.getColumnName(),
                    new Param())
            );
        }

        return this;
    }

    public CreateQueryBuilder<T> createForeign() throws Exception {
        for (ForeignFieldType fieldType : tableInfo.toForeignFieldTypes()) {
            query.add(new UpdateValue(
                    fieldType.getColumnName(),
                    new Param())
            );
        }

        return this;
    }

    public CreateQuery query() {
        return query;
    }
}
