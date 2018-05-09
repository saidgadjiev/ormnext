package ru.saidgadjiev.ormnext.core.stamentexecutor.object;

import ru.saidgadjiev.ormnext.core.dao.Session;
import ru.saidgadjiev.ormnext.core.dao.SessionManager;
import ru.saidgadjiev.ormnext.core.logger.Log;
import ru.saidgadjiev.ormnext.core.logger.LoggerFactory;
import ru.saidgadjiev.proxymaker.MethodHandler;

import java.lang.reflect.Method;

public class OrmNextMethodHandler implements MethodHandler {

    private static final Log LOG = LoggerFactory.getLogger(OrmNextMethodHandler.class);

    private boolean initialized = false;

    private Object target;

    private final Class<?> entityClass;

    private final Object id;

    private SessionManager sessionManager;

    public OrmNextMethodHandler(SessionManager sessionManager, Class<?> entityClass, Object id) {
        this.sessionManager = sessionManager;
        this.entityClass = entityClass;
        this.id = id;
    }

    @Override
    public Object invoke(Method method, Object[] args) {
        try {
            if (!initialized) {
                Session session = sessionManager.createSession();
                target = session.queryForId(entityClass, id);
                initialized = true;
                session.close();
                //LOG.debug("Entity " + entityClass + " with id " + id + " lazy initialized");
            }

            return method.invoke(target, args);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}
