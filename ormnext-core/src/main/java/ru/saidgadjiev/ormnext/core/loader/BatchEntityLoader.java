package ru.saidgadjiev.ormnext.core.loader;

import ru.saidgadjiev.ormnext.core.connection.DatabaseResults;
import ru.saidgadjiev.ormnext.core.dao.Dao;
import ru.saidgadjiev.ormnext.core.dao.DatabaseEngine;
import ru.saidgadjiev.ormnext.core.dao.Session;
import ru.saidgadjiev.ormnext.core.dao.SessionManager;
import ru.saidgadjiev.ormnext.core.field.fieldtype.DatabaseColumnType;
import ru.saidgadjiev.ormnext.core.query.criteria.impl.DeleteStatement;
import ru.saidgadjiev.ormnext.core.query.criteria.impl.SelectStatement;
import ru.saidgadjiev.ormnext.core.query.space.EntityQuerySpace;
import ru.saidgadjiev.ormnext.core.query.visitor.element.CreateQuery;
import ru.saidgadjiev.ormnext.core.query.visitor.element.DeleteQuery;
import ru.saidgadjiev.ormnext.core.query.visitor.element.SqlStatement;
import ru.saidgadjiev.ormnext.core.query.visitor.element.UpdateQuery;
import ru.saidgadjiev.ormnext.core.table.internal.metamodel.DatabaseEntityMetadata;
import ru.saidgadjiev.ormnext.core.table.internal.metamodel.MetaModel;
import ru.saidgadjiev.ormnext.core.table.internal.persister.DatabaseEntityPersister;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import static ru.saidgadjiev.ormnext.core.utils.ArgumentUtils.*;

/**
 * Batch entity loader. Use for batch query execute.
 */
public class BatchEntityLoader implements EntityLoader {

    /**
     * Cache helper.
     */
    private final CacheHelper cacheHelper;

    /**
     * Current meta model.
     */
    private MetaModel metaModel;

    /**
     * Database engine.
     */
    private DatabaseEngine<?> databaseEngine;

    /**
     * Batch statements.
     */
    private List<SqlStatement> batchStatements = new ArrayList<>();

    /**
     * Post batch executes. This will be executed after execute sql statements {@link #batchStatements}.
     */
    private List<Callable<Void>> postBatchExecutes = new ArrayList<>();

    /**
     * Create a new instance.
     *
     * @param sessionManager session manager
     */
    public BatchEntityLoader(SessionManager sessionManager) {
        this.cacheHelper = sessionManager.cacheHelper();
        this.databaseEngine = sessionManager.getDatabaseEngine();
        this.metaModel = sessionManager.getMetaModel();
    }

    @Override
    public int create(Session session, Object object) throws SQLException {
        DatabaseEntityPersister entityPersister = metaModel.getPersister(object.getClass());

        DatabaseColumnType primaryKeyType = entityPersister.getMetadata().getPrimaryKeyColumnType();
        Map<DatabaseColumnType, Argument> argumentMap = ejectForCreate(object, entityPersister.getMetadata());
        EntityQuerySpace entityQuerySpace = entityPersister.getEntityQuerySpace();
        CreateQuery createQuery = entityQuerySpace.getCreateQueryCompiledStatement(argumentMap);

        batchStatements.add(createQuery);
        if (!primaryKeyType.generated()) {
            postBatchExecutes.add(() -> {
                cacheHelper.saveToCache(null, object);

                return null;
            });
        }

        return 0;
    }

    @Override
    public int create(Session session, Object... objects) throws SQLException {
        if (objects.length == 0) {
            return 0;
        }
        DatabaseEntityPersister entityPersister = metaModel.getPersister(objects.getClass());
        DatabaseColumnType primaryKeyType = entityPersister.getMetadata().getPrimaryKeyColumnType();

        for (Object object : objects) {
            Map<DatabaseColumnType, Argument> argumentMap = ejectForCreate(object, entityPersister.getMetadata());
            EntityQuerySpace entityQuerySpace = entityPersister.getEntityQuerySpace();
            CreateQuery createQuery = entityQuerySpace.getCreateQueryCompiledStatement(argumentMap);

            batchStatements.add(createQuery);
        }
        if (!primaryKeyType.generated()) {
            postBatchExecutes.add(() -> {
                cacheHelper.saveToCache(objects, objects[0].getClass());

                return null;
            });
        }

        return 0;
    }

    @Override
    public boolean createTable(Session session, Class<?> tClass, boolean ifNotExist) {
        throw new UnsupportedOperationException("Not supported for batch execute");
    }

    @Override
    public boolean dropTable(Session session, Class<?> tClass, boolean ifExist) {
        throw new UnsupportedOperationException("Not supported for batch execute");
    }

    @Override
    public int update(Session session, Object object) throws SQLException {
        DatabaseEntityPersister entityPersister = metaModel.getPersister(object.getClass());
        DatabaseEntityMetadata<?> entityMetadata = entityPersister.getMetadata();

        Map<DatabaseColumnType, Argument> arguments = ejectForUpdate(object, entityMetadata);
        Argument id = eject(object, entityMetadata.getPrimaryKeyColumnType());

        UpdateQuery updateQuery = entityPersister.getEntityQuerySpace().getUpdateByIdCompiledQuery(
                arguments,
                eject(object, entityMetadata.getPrimaryKeyColumnType())
        );

        batchStatements.add(updateQuery);
        postBatchExecutes.add(() -> {
            cacheHelper.saveToCache(id, object);

            return null;
        });

        return 0;
    }

    @Override
    public int delete(Session session, Object object) throws SQLException {
        DatabaseEntityPersister entityPersister = metaModel.getPersister(object.getClass());
        Argument id = eject(object, entityPersister.getMetadata().getPrimaryKeyColumnType());

        DeleteQuery deleteQuery = entityPersister.getEntityQuerySpace().getDeleteByIdCompiledQuery(id);

        batchStatements.add(deleteQuery);
        postBatchExecutes.add(() -> {
            cacheHelper.delete(object.getClass(), id);

            return null;
        });

        return 0;
    }

    @Override
    public int deleteById(Session session, Class<?> tClass, Object id) throws SQLException {
        DatabaseEntityPersister entityPersister = metaModel.getPersister(tClass);
        Argument idArgument = processConvertersToSqlValue(
                id,
                entityPersister.getMetadata().getPrimaryKeyColumnType()
        );
        DeleteQuery deleteQuery = entityPersister.getEntityQuerySpace().getDeleteByIdCompiledQuery(idArgument);

        batchStatements.add(deleteQuery);
        postBatchExecutes.add(() -> {
            cacheHelper.delete(tClass, id);

            return null;
        });

        return 0;
    }

    @Override
    public <T> T queryForId(Session session, Class<T> tClass, Object id) {
        throw new UnsupportedOperationException("Not supported for batch execute");
    }

    @Override
    public <T> List<T> queryForAll(Session session, Class<T> tClass) {
        throw new UnsupportedOperationException("Not supported for batch execute");
    }

    @Override
    public boolean refresh(Session session, Object object) {
        throw new UnsupportedOperationException("Not supported for batch execute");
    }

    @Override
    public void createIndexes(Session session, Class<?> tClass) {
        throw new UnsupportedOperationException("Not supported for batch execute");
    }

    @Override
    public void dropIndexes(Session session, Class<?> tClass) {
        throw new UnsupportedOperationException("Not supported for batch execute");
    }

    @Override
    public long countOff(Session session, Class<?> tClass) {
        throw new UnsupportedOperationException("Not supported for batch execute");
    }

    @Override
    public <T> List<T> list(Session session, SelectStatement<T> selectStatement) {
        throw new UnsupportedOperationException("Not supported for batch execute");
    }

    @Override
    public long queryForLong(Session session, SelectStatement<?> selectStatement) {
        throw new UnsupportedOperationException("Not supported for batch execute");
    }

    @Override
    public DatabaseResults query(Session session, String query) {
        throw new UnsupportedOperationException("Not supported for batch execute");
    }

    @Override
    public void batch() {

    }

    @Override
    public int[] executeBatch(Session session) throws SQLException {
        int[] batchResults = databaseEngine.executeBatch(session.getConnection(), batchStatements);

        batchStatements.clear();
        try {
            for (Callable<Void> callable : postBatchExecutes) {
                callable.call();
            }
        } catch (Exception ex) {
            throw new SQLException(ex);
        } finally {
            postBatchExecutes.clear();
        }

        return batchResults;
    }

    @Override
    public int clearTable(Session session, Class<?> entityClass) {
        throw new UnsupportedOperationException("Not supported for batch execute");
    }

    @Override
    public Dao.CreateOrUpdateStatus createOrUpdate(Session session, Object object) {
        throw new UnsupportedOperationException("Not supported for batch execute");
    }

    @Override
    public boolean exist(Session session, Class<?> entityClass, Object id) {
        throw new UnsupportedOperationException("Not supported for batch execute");
    }

    @Override
    public int delete(Session session, DeleteStatement deleteStatement) {
        throw new UnsupportedOperationException("Not supported for batch execute");
    }
}
