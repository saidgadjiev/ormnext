package ru.saidgadjiev.ormnext.core.loader;

import ru.saidgadjiev.ormnext.core.connectionsource.DatabaseConnection;
import ru.saidgadjiev.ormnext.core.connectionsource.DatabaseResults;
import ru.saidgadjiev.ormnext.core.dao.DatabaseEngine;
import ru.saidgadjiev.ormnext.core.dao.Session;
import ru.saidgadjiev.ormnext.core.field.fieldtype.ForeignCollectionColumnType;
import ru.saidgadjiev.ormnext.core.field.fieldtype.ForeignColumnType;
import ru.saidgadjiev.ormnext.core.field.fieldtype.IDatabaseColumnType;
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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import static ru.saidgadjiev.ormnext.core.utils.ArgumentUtils.*;

public class BatchEntityLoader implements EntityLoader {

    /**
     * Current meta model.
     */
    private MetaModel metaModel;

    private Session session;
    /**
     * Current database engine.
     *
     * @see DatabaseEngine
     */
    private DatabaseEngine<?> databaseEngine;

    private List<SqlStatement> batchStatements = new ArrayList<>();

    private List<Callable<Void>> postBatchExecutes = new ArrayList<>();

    /**
     * Create a new instance.
     *
     * @param metaModel      current meta model
     * @param databaseEngine target database engine
     */
    public BatchEntityLoader(Session session, MetaModel metaModel, DatabaseEngine<?> databaseEngine) {
        this.session = session;
        this.databaseEngine = databaseEngine;
        this.metaModel = metaModel;
    }

    @Override
    public int create(DatabaseConnection connection, Object object) throws SQLException {
        DatabaseEntityPersister entityPersister = metaModel.getPersister(object.getClass());

        foreignAutoCreate(object, entityPersister.getMetadata());

        IDatabaseColumnType primaryKeyType = entityPersister.getMetadata().getPrimaryKeyColumnType();
        Map<IDatabaseColumnType, Argument> argumentMap = ejectForCreate(object, entityPersister.getMetadata());
        EntityQuerySpace entityQuerySpace = entityPersister.getEntityQuerySpace();
        CreateQuery createQuery = entityQuerySpace.getCreateQueryCompiledStatement(argumentMap);

        batchStatements.add(createQuery);
        if (!primaryKeyType.isGenerated()) {
            postBatchExecutes.add(() -> {
                session.cacheHelper().saveToCache(null, object);

                return null;
            });
        }

        return 0;
    }

    @Override
    public int create(DatabaseConnection connection, Object[] objects) throws SQLException {
        if (objects.length == 0) {
            return 0;
        }
        DatabaseEntityPersister entityPersister = metaModel.getPersister(objects.getClass());
        IDatabaseColumnType primaryKeyType = entityPersister.getMetadata().getPrimaryKeyColumnType();

        for (Object object : objects) {
            foreignAutoCreate(object, entityPersister.getMetadata());
            Map<IDatabaseColumnType, Argument> argumentMap = ejectForCreate(object, entityPersister.getMetadata());
            EntityQuerySpace entityQuerySpace = entityPersister.getEntityQuerySpace();
            CreateQuery createQuery = entityQuerySpace.getCreateQueryCompiledStatement(argumentMap);

            batchStatements.add(createQuery);
        }
        if (!primaryKeyType.isGenerated()) {
            postBatchExecutes.add(() -> {
                session.cacheHelper().saveToCache(objects, objects[0].getClass());

                return null;
            });
        }

        return 0;
    }

    @Override
    public boolean createTable(DatabaseConnection connection, Class<?> tClass, boolean ifNotExist) {
        throw new UnsupportedOperationException("Not supported for batch execute");
    }

    @Override
    public boolean dropTable(DatabaseConnection connection, Class<?> tClass, boolean ifExist) {
        throw new UnsupportedOperationException("Not supported for batch execute");
    }

    @Override
    public int update(DatabaseConnection connection, Object object) throws SQLException {
        DatabaseEntityPersister entityPersister = metaModel.getPersister(object.getClass());
        DatabaseEntityMetadata<?> entityMetadata = entityPersister.getMetadata();

        Map<IDatabaseColumnType, Argument> arguments = ejectForUpdate(object, entityMetadata);
        Argument id = eject(object, entityMetadata.getPrimaryKeyColumnType());

        UpdateQuery updateQuery = entityPersister.getEntityQuerySpace().getUpdateByIdCompiledQuery(
                arguments,
                eject(object, entityMetadata.getPrimaryKeyColumnType())
        );

        batchStatements.add(updateQuery);
        postBatchExecutes.add(() -> {
            session.cacheHelper().saveToCache(id, object);

            return null;
        });

        return 0;
    }

    @Override
    public int delete(DatabaseConnection connection, Object object) throws SQLException {
        DatabaseEntityPersister entityPersister = metaModel.getPersister(object.getClass());
        Argument id = eject(object, entityPersister.getMetadata().getPrimaryKeyColumnType());

        DeleteQuery deleteQuery = entityPersister.getEntityQuerySpace().getDeleteByIdCompiledQuery(id);

        batchStatements.add(deleteQuery);
        postBatchExecutes.add(() -> {
            session.cacheHelper().delete(object.getClass(), id);

            return null;
        });

        return 0;
    }

    @Override
    public int deleteById(DatabaseConnection connection, Class<?> tClass, Object id) throws SQLException {
        DatabaseEntityPersister entityPersister = metaModel.getPersister(tClass);
        Argument idArgument = processConvertersToSqlValue(
                id,
                entityPersister.getMetadata().getPrimaryKeyColumnType()
        );
        DeleteQuery deleteQuery = entityPersister.getEntityQuerySpace().getDeleteByIdCompiledQuery(idArgument);

        batchStatements.add(deleteQuery);
        postBatchExecutes.add(() -> {
            session.cacheHelper().delete(tClass, id);

            return null;
        });

        return 0;
    }

    @Override
    public <T> T queryForId(DatabaseConnection connection, Class<T> tClass, Object id) {
        throw new UnsupportedOperationException("Not supported for batch execute");
    }

    @Override
    public <T> List<T> queryForAll(DatabaseConnection connection, Class<T> tClass) {
        throw new UnsupportedOperationException("Not supported for batch execute");
    }

    @Override
    public boolean refresh(DatabaseConnection databaseConnection, Object object) throws SQLException {
        throw new UnsupportedOperationException("Not supported for batch execute");
    }

    @Override
    public void createIndexes(DatabaseConnection connection, Class<?> tClass) {
        throw new UnsupportedOperationException("Not supported for batch execute");
    }

    @Override
    public void dropIndexes(DatabaseConnection connection, Class<?> tClass) {
        throw new UnsupportedOperationException("Not supported for batch execute");
    }

    @Override
    public long countOff(DatabaseConnection connection, Class<?> tClass) {
        throw new UnsupportedOperationException("Not supported for batch execute");
    }

    @Override
    public <T> List<T> list(DatabaseConnection connection, SelectStatement<T> selectStatement) {
        throw new UnsupportedOperationException("Not supported for batch execute");
    }

    @Override
    public long queryForLong(DatabaseConnection connection, SelectStatement<?> selectStatement) {
        throw new UnsupportedOperationException("Not supported for batch execute");
    }

    @Override
    public DatabaseResults query(DatabaseConnection databaseConnection, String query) {
        throw new UnsupportedOperationException("Not supported for batch execute");
    }

    @Override
    public void batch() {

    }

    @Override
    public int[] executeBatch(DatabaseConnection<?> databaseConnection) throws SQLException {
        int[] batchResults = databaseEngine.executeBatch(databaseConnection, batchStatements);

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

    private void foreignAutoCreate(Object object, DatabaseEntityMetadata<?> entityMetadata) throws SQLException {
        for (ForeignColumnType foreignColumnType : entityMetadata.toForeignColumnTypes()) {
            Object foreignObject = foreignColumnType.access(object);

            if (foreignObject != null && foreignColumnType.foreignAutoCreate()) {
                session.create(foreignObject);
            }
        }
        for (ForeignCollectionColumnType foreignCollectionColumnType : entityMetadata.toForeignCollectionColumnTypes()) {
            if (foreignCollectionColumnType.foreignAutoCreate()) {
                for (Object foreignObject : (Collection) foreignCollectionColumnType.access(object)) {
                    foreignCollectionColumnType.getForeignColumnType().assign(foreignObject, object);

                    session.create(foreignObject);
                }
            }
        }
    }
}
