package ru.saidgadjiev.orm.next.core.stamentexecutor.object.operation;

import ru.saidgadjiev.orm.next.core.dao.SessionManagerImpl;
import ru.saidgadjiev.orm.next.core.dao.Session;
import ru.saidgadjiev.orm.next.core.field.fieldtype.ForeignColumnType;
import ru.saidgadjiev.orm.next.core.support.ConnectionSource;
import ru.saidgadjiev.orm.next.core.table.DatabaseEntityMetadata;
import ru.saidgadjiev.orm.next.core.table.TableInfoManager;

/**
 * Created by said on 10.02.2018.
 */
public class ForeignCreator<O> implements IObjectOperation<Void, O> {

    private ConnectionSource source;

    public ForeignCreator(ConnectionSource source) {
        this.source = source;
    }

    @Override
    public Void execute(O object) throws Exception {
        DatabaseEntityMetadata<O> databaseEntityMetadata = TableInfoManager.buildOrGet((Class<O>) object.getClass());

        for (ForeignColumnType fieldType : databaseEntityMetadata.toForeignFieldTypes()) {
            Object foreignObject = fieldType.access(object);

            if (foreignObject != null && fieldType.isForeignAutoCreate()) {
                Session foreignDao = new SessionManagerImpl(source, null).getCurrentSession();

                foreignDao.create(foreignObject);
            }
        }

        return null;
    }
}
