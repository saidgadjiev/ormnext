package ru.saidgadjiev.ormnext.core.dao.context;

import ru.saidgadjiev.ormnext.core.dao.Session;
import ru.saidgadjiev.ormnext.core.dao.SessionManager;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Thread local session context.
 *
 * @author Said Gadjiev
 */
public class ThreadLocalCurrentSessionContext implements CurrentSessionContext {

    /**
     * Current session manager.
     */
    private final SessionManager sessionManager;

    /**
     * Session holder thread local.
     */
    private static final ThreadLocal<Map<SessionManager, Session>> CONTEXT_TL =
            ThreadLocal.withInitial(HashMap::new);

    /**
     * Create a new instance.
     *
     * @param sessionManager target session manager
     */
    public ThreadLocalCurrentSessionContext(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public Session currentSession() throws SQLException {
        Session current = getSession(sessionManager);

        if (current == null) {
            current = sessionManager.createSession();

            sessionMap().put(sessionManager, current);
        }

        return current;
    }

    /**
     * Obtain session from thread local.
     *
     * @param sessionManager target session manager
     * @return current session
     */
    private Session getSession(SessionManager sessionManager) {
        return sessionMap().get(sessionManager);
    }

    /**
     * Obtain session map.
     *
     * @return session map
     */
    private Map<SessionManager, Session> sessionMap() {
        return CONTEXT_TL.get();
    }
}
