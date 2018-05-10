package ru.saidgadjiev.orm.next.core.stamentexecutor.object;

import ru.saidgadjiev.orm.next.core.dao.Dao;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class OrmNextProxy implements InvocationHandler {

    private boolean initialized = false;

    private Object target;

    private Dao dao;

    private final Class<?> entityClass;

    private final Object id;

    public OrmNextProxy(Dao dao, Class<?> entityClass, Object id) {
        this.dao = dao;
        this.entityClass = entityClass;
        this.id = id;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (!initialized) {
            target = dao.queryForId(entityClass, id);
            initialized = true;
        }

        return method.invoke(target, args);
    }

}
