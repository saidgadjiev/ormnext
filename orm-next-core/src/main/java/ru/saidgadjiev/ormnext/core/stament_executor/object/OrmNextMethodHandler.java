package ru.saidgadjiev.ormnext.core.stament_executor.object;

import ru.saidgadjiev.ormnext.core.dao.Session;
import ru.saidgadjiev.ormnext.core.dao.SessionManager;
import ru.saidgadjiev.ormnext.core.logger.Log;
import ru.saidgadjiev.ormnext.core.logger.LoggerFactory;
import ru.saidgadjiev.proxymaker.MethodHandler;

import java.lang.reflect.Method;

/**
 * This implementation {@link MethodHandler}
 * use for lazy fetch for {@link ru.saidgadjiev.ormnext.core.field.ForeignColumn}.
 */
public class OrmNextMethodHandler implements MethodHandler {

    /**
     * Logger.
     */
    private static final Log LOG = LoggerFactory.getLogger(OrmNextMethodHandler.class);

    /**
     * Initialized state.
     */
    private boolean initialized = false;

    /**
     * Target instance which will be lazy instantiated.
     */
    private Object target;

    /**
     * Entity class.
     */
    private final Class<?> entityClass;

    /**
     * Entity object id.
     */
    private final Object id;

    /**
     * Session manager.
     */
    private SessionManager sessionManager;

    /**
     * Create a new instance for lazy initialize entity object.
     * @param sessionManager session manager
     * @param entityClass entity class
     * @param id entity object id
     */
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
                LOG.debug("Entity %s with id %s lazy initialized", entityClass.getName(), id);
            }

            return method.invoke(target, args);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}
