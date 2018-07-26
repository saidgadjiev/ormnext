package ru.saidgadjiev.ormnext.core.dao.context;

import ru.saidgadjiev.ormnext.core.dao.Session;

import java.sql.SQLException;

public interface CurrentSessionContext {

    Session currentSession() throws SQLException;
}
