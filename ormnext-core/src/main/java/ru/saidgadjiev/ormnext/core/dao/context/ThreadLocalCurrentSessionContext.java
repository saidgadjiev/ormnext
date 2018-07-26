package ru.saidgadjiev.ormnext.core.dao.context;

import ru.saidgadjiev.ormnext.core.dao.Session;
import ru.saidgadjiev.ormnext.core.dao.SessionManager;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ThreadLocalCurrentSessionContext implements CurrentSessionContext {

    private final SessionManager sessionManager;

    private static final ThreadLocal<Map<SessionManager, Session>> CONTEXT_TL =
            ThreadLocal.withInitial(HashMap::new);

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

    private Session getSession(SessionManager sessionManager) {
        return sessionMap().get(sessionManager);
    }

    private Map<SessionManager, Session> sessionMap() {
        return CONTEXT_TL.get();
    }
}
