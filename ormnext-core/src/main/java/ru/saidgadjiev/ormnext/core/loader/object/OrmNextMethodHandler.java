package ru.saidgadjiev.ormnext.core.loader.object;

import ru.saidgadjiev.ormnext.core.dao.Session;
import ru.saidgadjiev.ormnext.core.dao.SessionManager;
import ru.saidgadjiev.ormnext.core.logger.Log;
import ru.saidgadjiev.ormnext.core.logger.LoggerFactory;
import ru.saidgadjiev.ormnext.core.query.criteria.impl.Criteria;
import ru.saidgadjiev.ormnext.core.query.criteria.impl.Restrictions;
import ru.saidgadjiev.ormnext.core.query.criteria.impl.SelectStatement;
import ru.saidgadjiev.proxymaker.MethodHandler;

import java.lang.reflect.Method;

/**
 * This implementation {@link MethodHandler}
 * use for lazy fetch for {@link ru.saidgadjiev.ormnext.core.field.ForeignColumn}.
 *
 * @author Said Gadjiev
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
     * Key property name.
     */
    private String keyPropertyName;
    /**
     * Entity object key.
     */
    private final Object key;

    /**
     * Session manager.
     */
    private SessionManager sessionManager;

    /**
     * Create a new instance for lazy initialize entity object.
     *
     * @param sessionManager  target session manager
     * @param entityClass     target entity class
     * @param keyPropertyName target entity object key property name
     * @param key             target entity object key
     */
    public OrmNextMethodHandler(SessionManager sessionManager,
                                Class<?> entityClass,
                                String keyPropertyName,
                                Object key) {
        this.sessionManager = sessionManager;
        this.entityClass = entityClass;
        this.keyPropertyName = keyPropertyName;
        this.key = key;
    }

    @Override
    public Object invoke(Method method, Object[] args) {
        try {
            if (!initialized) {
                SelectStatement<?> selectStatement = new SelectStatement<>(entityClass);

                selectStatement.where(new Criteria().add(Restrictions.eq(keyPropertyName, key)));
                Session session = sessionManager.createSession();
                target = session.uniqueResult(selectStatement);
                initialized = true;
                session.close();
                LOG.debug("Entity %s with key %s lazy initialized", entityClass.getName(), key);
            }

            return method.invoke(target, args);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}
