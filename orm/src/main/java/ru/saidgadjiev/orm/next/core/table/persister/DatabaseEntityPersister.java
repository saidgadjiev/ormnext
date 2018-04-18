package ru.saidgadjiev.orm.next.core.table.persister;

import ru.saidgadjiev.orm.next.core.dao.Session;
import ru.saidgadjiev.orm.next.core.dao.metamodel.MetaModel;
import ru.saidgadjiev.orm.next.core.query.core.clause.from.FromExpression;
import ru.saidgadjiev.orm.next.core.query.core.clause.select.SelectColumnsList;
import ru.saidgadjiev.orm.next.core.stamentexecutor.DatabaseResults;
import ru.saidgadjiev.orm.next.core.stamentexecutor.alias.EntityAliases;
import ru.saidgadjiev.orm.next.core.table.DatabaseEntityMetadata;

import java.sql.SQLException;
import java.util.List;

public interface DatabaseEntityPersister {

    List<Object> load(Session session, DatabaseResults databaseResults) throws SQLException;

    void initialize();

    DatabaseEntityMetadata<?> getMetadata();

    Object instance();

    EntityAliases getAliases();

    FromExpression getFromExpression();

    SelectColumnsList getSelectColumnsList();

    MetaModel getMetaModel();
}
