package ru.saidgadjiev.orm.next.core.stamentexecutor.object.operation;

import ru.saidgadjiev.orm.next.core.dao.Session;
import ru.saidgadjiev.orm.next.core.field.fieldtype.ForeignColumnType;
import ru.saidgadjiev.orm.next.core.table.internal.metamodel.DatabaseEntityMetadata;
import ru.saidgadjiev.orm.next.core.table.internal.metamodel.MetaModel;

/**
 * Created by said on 10.02.2018.
 */
public class ForeignCreator<O> implements IObjectOperation<Void, Object> {

    private Session session;

    private MetaModel metaModel;

    public ForeignCreator(Session session, MetaModel metaModel) {
        this.session = session;
        this.metaModel = metaModel;
    }

    @Override
    public Void execute(Object object) throws Exception {
        DatabaseEntityMetadata<?> databaseEntityMetadata = metaModel.getPersister(object.getClass()).getMetadata();

        for (ForeignColumnType fieldType : databaseEntityMetadata.toForeignFieldTypes()) {
            Object foreignObject = fieldType.access(object);

            if (foreignObject != null && fieldType.isForeignAutoCreate()) {
                session.create(foreignObject);
            }
        }

        return null;
    }
}
