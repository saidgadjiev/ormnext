package ru.saidgadjiev.orm.next.core.stament_executor.object.operation;

import ru.saidgadjiev.orm.next.core.dao.BaseDaoImpl;
import ru.saidgadjiev.orm.next.core.dao.Dao;
import ru.saidgadjiev.orm.next.core.dao.DaoManager;
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
            TableInfo<?> foreignTableInfo = TableInfo.build(fieldType.getForeignFieldClass());

            if (foreignObject != null && fieldType.isForeignAutoCreate()) {
                Dao<Object, ?> foreignDao = (BaseDaoImpl<Object, ?>) DaoManager.createDAOWithTableInfo(source, foreignTableInfo);

                foreignDao.create(foreignObject);
            }
        }

        return null;
    }
}
