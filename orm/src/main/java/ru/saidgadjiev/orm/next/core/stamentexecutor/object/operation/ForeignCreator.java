package ru.saidgadjiev.orm.next.core.stamentexecutor.object.operation;

import ru.saidgadjiev.orm.next.core.dao.Dao;
import ru.saidgadjiev.orm.next.core.field.fieldtype.ForeignColumnType;
import ru.saidgadjiev.orm.next.core.table.internal.metamodel.DatabaseEntityMetadata;
import ru.saidgadjiev.orm.next.core.table.internal.metamodel.MetaModel;

/**
 * Created by said on 10.02.2018.
 */
public class ForeignCreator<O> implements IObjectOperation<Void, Object> {

    private Dao dao;

    private MetaModel metaModel;

    public ForeignCreator(Dao dao, MetaModel metaModel) {
        this.dao = dao;
        this.metaModel = metaModel;
    }

    @Override
    public Void execute(Object object) throws Exception {
        DatabaseEntityMetadata<?> databaseEntityMetadata = metaModel.getPersister(object.getClass()).getMetadata();

        for (ForeignColumnType fieldType : databaseEntityMetadata.toForeignFieldTypes()) {
            Object foreignObject = fieldType.access(object);

            if (foreignObject != null && fieldType.isForeignAutoCreate()) {
                dao.create(foreignObject);
            }
        }

        return null;
    }
}
