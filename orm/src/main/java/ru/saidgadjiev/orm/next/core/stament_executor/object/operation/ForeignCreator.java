package ru.saidgadjiev.orm.next.core.stament_executor.object.operation;

import ru.saidgadjiev.orm.next.core.dao.BaseSessionManagerImpl;
import ru.saidgadjiev.orm.next.core.dao.Session;
import ru.saidgadjiev.orm.next.core.field.field_type.ForeignFieldType;
import ru.saidgadjiev.orm.next.core.support.ConnectionSource;
import ru.saidgadjiev.orm.next.core.table.TableInfo;

/**
 * Created by said on 10.02.2018.
 */
public class ForeignCreator<O> implements IObjectOperation<Void, O> {

    private TableInfo<O> tableInfo;

    private ConnectionSource source;

    public ForeignCreator(TableInfo<O> tableInfo, ConnectionSource source) {
        this.tableInfo = tableInfo;
        this.source = source;
    }

    @Override
    public Void execute(O object) throws Exception {
        for (ForeignFieldType fieldType : tableInfo.toForeignFieldTypes()) {
            Object foreignObject = fieldType.access(object);

            if (foreignObject != null && fieldType.isForeignAutoCreate()) {
                Session<Object, ?> foreignDao = new BaseSessionManagerImpl(source).forClass(fieldType.getForeignFieldClass());

                foreignDao.create(foreignObject);
            }
        }

        return null;
    }
}
