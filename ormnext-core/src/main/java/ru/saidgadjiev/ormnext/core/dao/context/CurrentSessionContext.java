package ru.saidgadjiev.ormnext.core.dao.context;

import ru.saidgadjiev.ormnext.core.dao.Session;

import java.sql.SQLException;

/**
 * Context for obtain current session.
 *
 * @author Said Gadjiev
 */
public interface CurrentSessionContext {

    /**
     * Obtain or create session.
     *
     * @return current session
     * @throws SQLException any exceptions
     */
    Session currentSession() throws SQLException;
}
