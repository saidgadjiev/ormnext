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
public class ForeignCreator implements IObjectOperation<Void> {

    private ConnectionSource source;

    public ForeignCreator(ConnectionSource source) {
        this.source = source;
    }

    @Override
    public<O> Void execute(TableInfo<O> tableInfo, O object) throws Exception {
        for (ForeignFieldType fieldType : tableInfo.toForeignFieldTypes()) {
            Object foreignObject = fieldType.access(object);
            TableInfo<?> foreignTableInfo = TableInfo.build(fieldType.getForeignFieldClass());

            if (foreignObject != null && fieldType.isForeignAutoCreate()) {
                Dao foreignDao = DaoManager.createDAOWithTableInfo(source, foreignTableInfo);

                foreignDao.create(foreignObject);
            }
        }

        return null;
    }
}
