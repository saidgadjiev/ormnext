package ru.saidgadjiev.orm.next.core.stamentexecutor.rowreader;

import ru.saidgadjiev.orm.next.core.stamentexecutor.ResultSetContext;
import ru.saidgadjiev.orm.next.core.stamentexecutor.alias.CollectionEntityAliases;

import java.sql.SQLException;

public class CollectionInitializer  {

    private final CollectionEntityAliases aliases;

    public CollectionInitializer(CollectionEntityAliases aliases) {
        this.aliases = aliases;
    }

    public void startRead(ResultSetContext resultSetContext) throws SQLException {
        resultSetContext.
    }

    public void finishRead(ResultSetContext resultSetContext) throws SQLException {

    }
}
