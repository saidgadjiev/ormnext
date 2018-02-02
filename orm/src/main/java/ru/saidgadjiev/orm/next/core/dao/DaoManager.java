package ru.saidgadjiev.orm.next.core.dao;

import ru.saidgadjiev.orm.next.core.support.ConnectionSource;
import ru.saidgadjiev.orm.next.core.table.DBTable;
import ru.saidgadjiev.orm.next.core.table.TableInfo;
import ru.saidgadjiev.up.cache.core.Cache;
import ru.saidgadjiev.up.cache.core.CacheBuilder;

import java.lang.reflect.Constructor;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Класс для создания DAO
 */
public class DaoManager {

    private static final Cache<Class<?>, Dao<?, ?>> CACHE = CacheBuilder.newRefenceCacheBuilder().build();

    public static <D extends Dao<T, ?>, T> D createDAO(ConnectionSource dataSource, Class<T> clazz) throws SQLException {
        try {
            Optional<D> lookupDao = (Optional<D>) lookupDao(clazz);

            if (lookupDao.isPresent()) {
                return lookupDao.get();
            }
            TableInfo<T> tableInfo = TableInfo.TableInfoCache.build(clazz);

            if (clazz.isAnnotationPresent(DBTable.class) && clazz.getAnnotation(DBTable.class).daoClass() != BaseDaoImpl.class) {
                Class<? extends BaseDaoImpl> daoClass = clazz.getAnnotation(DBTable.class).daoClass();
                Optional<Constructor<?>> constructorOptional = finConstructor(daoClass, new Object[] {dataSource, tableInfo});

                if (constructorOptional.isPresent()) {
                    D dao = (D) constructorOptional.get().newInstance(dataSource, tableInfo);
                    CACHE.put(clazz, dao);

                    return dao;
                }
            }
            D dao = (D) BaseDaoImpl.createDao(dataSource, tableInfo);
            CACHE.put(clazz, dao);

            return dao;
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    private static Optional<Constructor<?>> finConstructor(Class<?> clazz, Object[] params) {
        for (Constructor<?> constructor: clazz.getDeclaredConstructors()) {
            final Class<?>[] parameterTypes = constructor.getParameterTypes();
            boolean match = true;

            for (int i = 0; i < parameterTypes.length; ++i) {
                if (!parameterTypes[i].isAssignableFrom(params[i].getClass())) {
                    match = false;

                    break;
                }
            }
            if (match) {
                return Optional.of(constructor);
            }
        }

        return Optional.empty();
    }

    static Optional<Dao<?, ?>> lookupDao(Class<?> clazz) {
        return Optional.ofNullable(CACHE.get(clazz));
    }

    static void registerDao(Class<?> clazz, Dao<?, ?> dao) {
        CACHE.put(clazz, dao);
    }

    public static <T, ID> Dao<T, ?> createDAOWithTableInfo(ConnectionSource dataSource, TableInfo<T> tableInfo) {
        return BaseDaoImpl.createDao(dataSource, tableInfo);
    }

    public synchronized static void clearCache() {
        CACHE.invalidateAll();
    }
}
