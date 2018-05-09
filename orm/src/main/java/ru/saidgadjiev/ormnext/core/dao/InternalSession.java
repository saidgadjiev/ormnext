package ru.saidgadjiev.ormnext.core.dao;

import ru.saidgadjiev.ormnext.core.dao.transaction.InternalTransaction;
import ru.saidgadjiev.ormnext.core.stamentexecutor.CacheHelper;

public interface InternalSession extends Session, InternalTransaction {

    CacheHelper cacheHelper();

    DatabaseEngine getDatabaseEngine();

    SessionManager getSessionManager();
}
