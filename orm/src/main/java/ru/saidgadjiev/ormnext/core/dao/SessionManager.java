package ru.saidgadjiev.ormnext.core.dao;

import ru.saidgadjiev.ormnext.core.cache.ObjectCache;
import ru.saidgadjiev.ormnext.core.table.internal.metamodel.MetaModel;

import java.sql.SQLException;

public interface SessionManager extends AutoCloseable {

    Session createSession() throws SQLException;

    MetaModel getMetaModel();

    void setObjectCache(ObjectCache objectCache);

    void enableDefaultCache();

    DatabaseEngine getDatabaseEngine();

    void close() throws SQLException;
}
