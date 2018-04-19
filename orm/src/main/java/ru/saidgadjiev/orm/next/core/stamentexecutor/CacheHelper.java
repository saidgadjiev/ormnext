package ru.saidgadjiev.orm.next.core.stamentexecutor;

import ru.saidgadjiev.orm.next.core.cache.CacheContext;
import ru.saidgadjiev.orm.next.core.cache.ObjectCache;
import ru.saidgadjiev.orm.next.core.field.fieldtype.DatabaseColumnType;
import ru.saidgadjiev.orm.next.core.field.fieldtype.IDatabaseColumnType;
import ru.saidgadjiev.orm.next.core.table.DatabaseEntityMetadata;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Collection;

public class CacheHelper {

    private CacheContext cacheContext;

    private CacheContext sessionCacheContext;

    public CacheHelper(CacheContext cacheContext, CacheContext sessionCacheContext) {
        this.cacheContext = cacheContext;
        this.sessionCacheContext = sessionCacheContext;
    }

    public void addToCache(Class<?> objectClass, Collection<?> objects) throws SQLException {
        if (cacheContext.isCaching(objectClass)) {
            ObjectCache objectCache = cacheContext.getObjectCache();

            for (Object object : objects) {
                IDatabaseColumnType primaryKeyColumnType = null;

                try {
                    objectCache.put(objectClass, primaryKeyColumnType.access(object), object);
                } catch (Exception ex) {
                    throw new SQLException(ex);
                }
            }
        }
    }

    public void update() throws SQLException {/*
        IDatabaseColumnType idFieldType = databaseEntityMetadata.getPrimaryKey();

        try {
            if (cacheContext.isCaching(databaseEntityMetadata.getTableClass())) {
                ObjectCache objectCache = cacheContext.getObjectCache();
                T cachedData = (T) objectCache.get(databaseEntityMetadata.getTableClass(), idFieldType.access(object));

                if (cachedData != null) {
                    copy(databaseEntityMetadata, object, cachedData);
                }
            }
        } catch (Exception ex) {
            throw new SQLException(ex);
        }*/
    }

    public void delete() throws SQLException {
        /*try {
            Object id = dbFieldType.access(object);

            if (cacheContext.isCaching(databaseEntityMetadata.getTableClass())) {
                cacheContext.getObjectCache().invalidate(databaseEntityMetadata.getTableClass(), id);
            }
        } catch (IllegalAccessException | InvocationTargetException ex) {
            throw new SQLException(ex);
        }*/
    }

    public void get() {
       /* Object instance = sessionCacheContext.getObjectCache().get(tClass, id);

        if (instance != null) {
            return (T) instance;
        }
        if (cacheContext.isCaching(databaseEntityMetadata.getTableClass())) {
            ObjectCache objectCache = cacheContext.getObjectCache();

            if (objectCache.contains(databaseEntityMetadata.getTableClass(), id)) {
                return (T) objectCache.get(databaseEntityMetadata.getTableClass(), id);
            }
        }*/
    }

    private<T> void copy(DatabaseEntityMetadata<T> databaseEntityMetadata, T srcObject, T destObject) throws Exception {
        for (DatabaseColumnType fieldType : databaseEntityMetadata.toDBFieldTypes()) {
            fieldType.assign(destObject, fieldType.access(srcObject));
        }
    }
}
