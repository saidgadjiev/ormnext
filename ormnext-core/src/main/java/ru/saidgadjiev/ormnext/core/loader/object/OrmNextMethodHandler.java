package ru.saidgadjiev.ormnext.core.loader.object;

import ru.saidgadjiev.ormnext.core.dao.Session;
import ru.saidgadjiev.ormnext.core.logger.Log;
import ru.saidgadjiev.ormnext.core.logger.LoggerFactory;
import ru.saidgadjiev.ormnext.core.query.criteria.impl.Criteria;
import ru.saidgadjiev.ormnext.core.query.criteria.impl.Restrictions;
import ru.saidgadjiev.ormnext.core.query.criteria.impl.SelectStatement;
import ru.saidgadjiev.proxymaker.MethodHandler;

import java.lang.reflect.Method;
import java.sql.SQLException;

/**
 * This implementation {@link MethodHandler}
 * use for lazy fetch for {@link ru.saidgadjiev.ormnext.core.field.ForeignColumn}.
 *
 * @author Said Gadjiev
 */
public class OrmNextMethodHandler implements MethodHandler, Lazy {

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
     * Session.
     */
    private Session session;

    /**
     * Create a new instance for lazy initialize entity object.
     *
     * @param session         target session
     * @param entityClass     target entity class
     * @param keyPropertyName target entity object key property name
     * @param key             target entity object key
     */
    public OrmNextMethodHandler(Session session,
                                Class<?> entityClass,
                                String keyPropertyName,
                                Object key) {
        this.session = session;
        this.entityClass = entityClass;
        this.keyPropertyName = keyPropertyName;
        this.key = key;
    }

    @Override
    public Object invoke(Method method, Object[] args) {
        try {
            initialize(session);

            return method.invoke(target, args);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Initialize lazy object.
     *
     * @param session target session
     * @throws SQLException any SQL exceptions
     */
    private void initialize(Session session) throws SQLException {
        if (!initialized) {
            SelectStatement<?> selectStatement = session.statementBuilder().createSelectStatement(entityClass);

            selectStatement.where(new Criteria().add(Restrictions.eq(keyPropertyName, key)));
            target = selectStatement.uniqueResult();
            initialized = true;
            session.close();
            LOG.debug("Entity %s with key %s lazy initialized", entityClass.getName(), key);
        }
    }

    @Override
    public void attach(Session session) {
        this.session = session;
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public void setNonInitialized() {
        initialized = false;
    }
}
