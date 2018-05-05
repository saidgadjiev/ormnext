package ru.saidgadjiev.ormnext.core.stamentexecutor.object;

import ru.saidgadjiev.ormnext.core.dao.Dao;
import ru.saidgadjiev.ormnext.core.logger.Log;
import ru.saidgadjiev.ormnext.core.logger.LoggerFactory;
import ru.saidgadjiev.ormnext.core.dao.Dao;
import ru.saidgadjiev.ormnext.core.logger.Log;
import ru.saidgadjiev.ormnext.core.logger.LoggerFactory;
import ru.saidgadjiev.proxymaker.MethodHandler;

import java.lang.reflect.Method;

public class OrmNextMethodHandler implements MethodHandler {

    private static final Log LOG = LoggerFactory.getLogger(OrmNextMethodHandler.class);

    private boolean initialized = false;

    private Object target;

    private Dao dao;

    private final Class<?> entityClass;

    private final Object id;

    public OrmNextMethodHandler(Dao dao, Class<?> entityClass, Object id) {
        this.dao = dao;
        this.entityClass = entityClass;
        this.id = id;
    }

    @Override
    public Object invoke(Method method, Object[] args) {
        try {
            if (!initialized) {
                target = dao.queryForId(entityClass, id);
                initialized = true;
                LOG.debug("Entity " + entityClass + " with id " + id + " lazy initialized");
            }

            return method.invoke(target, args);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}
