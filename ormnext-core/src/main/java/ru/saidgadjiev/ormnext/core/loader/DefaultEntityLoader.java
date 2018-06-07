package ru.saidgadjiev.ormnext.core.loader;

import ru.saidgadjiev.ormnext.core.connection.DatabaseResults;
import ru.saidgadjiev.ormnext.core.dao.DatabaseEngine;
import ru.saidgadjiev.ormnext.core.dao.Session;
import ru.saidgadjiev.ormnext.core.dao.SessionManager;
import ru.saidgadjiev.ormnext.core.exception.GeneratedValueNotFoundException;
import ru.saidgadjiev.ormnext.core.exception.PropertyNotFoundException;
import ru.saidgadjiev.ormnext.core.field.DataPersisterManager;
import ru.saidgadjiev.ormnext.core.field.datapersister.DataPersister;
import ru.saidgadjiev.ormnext.core.field.fieldtype.ForeignCollectionColumnTypeImpl;
import ru.saidgadjiev.ormnext.core.field.fieldtype.ForeignColumnTypeImpl;
import ru.saidgadjiev.ormnext.core.field.fieldtype.DatabaseColumnType;
import ru.saidgadjiev.ormnext.core.loader.rowreader.RowResult;
import ru.saidgadjiev.ormnext.core.query.criteria.impl.CriterionArgument;
import ru.saidgadjiev.ormnext.core.query.criteria.impl.SelectStatement;
import ru.saidgadjiev.ormnext.core.query.space.EntityQuerySpace;
import ru.saidgadjiev.ormnext.core.query.visitor.element.*;
import ru.saidgadjiev.ormnext.core.table.internal.metamodel.DatabaseEntityMetadata;
import ru.saidgadjiev.ormnext.core.table.internal.metamodel.MetaModel;
import ru.saidgadjiev.ormnext.core.table.internal.persister.DatabaseEntityPersister;
import ru.saidgadjiev.ormnext.core.utils.DatabaseEntityMetadataUtils;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static ru.saidgadjiev.ormnext.core.utils.ArgumentUtils.*;

/**
 * Executes SQL statements via {@link DatabaseEngine}.
 *
 * @author Said Gadjiev
 */
public class DefaultEntityLoader implements EntityLoader {

    /**
     * Current meta model.
     */
    private MetaModel metaModel;

    /**
     * Current database engine.
     *
     * @see DatabaseEngine
     */
    private DatabaseEngine<?> databaseEngine;

    /**
     * Cache helper.
     */
    private CacheHelper cacheHelper;

    /**
     * Create a new instance.
     *
     * @param sessionManager session manager
     */
    public DefaultEntityLoader(SessionManager sessionManager) {
        this.databaseEngine = sessionManager.getDatabaseEngine();
        this.metaModel = sessionManager.getMetaModel();
        this.cacheHelper = sessionManager.cacheHelper();
    }

    @Override
    public int create(Session session, Object object) throws SQLException {
        try {
            DatabaseEntityPersister entityPersister = metaModel.getPersister(object.getClass());
            DatabaseEntityMetadata<?> entityMetadata = entityPersister.getMetadata();

            foreignAutoCreate(session, object, entityMetadata);

            Map<DatabaseColumnType, Argument> argumentMap = ejectForCreate(object, entityMetadata);
            EntityQuerySpace entityQuerySpace = entityPersister.getEntityQuerySpace();
            CreateQuery createQuery = entityQuerySpace.getCreatedQuery(argumentMap.keySet());
            DatabaseColumnType idField = entityMetadata.getPrimaryKeyColumnType();

            GeneratedKey generatedKey = new KeyHolder();

            int result = databaseEngine.create(
                    session.getConnection(),
                    createQuery,
                    new HashMap<Integer, Argument>() {{
                        AtomicInteger index = new AtomicInteger();

                        argumentMap.forEach((key, value) -> put(index.incrementAndGet(), value));
                    }},
                    idField,
                    generatedKey
            );

            if (idField.generated()) {
                processGeneratedKey(generatedKey, idField, object);
            } else {
                cacheHelper.saveToCache(null, object);
            }

            foreignAutoCreateForeignCollection(session, object, entityMetadata);

            return result;
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    @Override
    public int create(Session session, Object[] objects) throws SQLException {
        if (objects.length == 0) {
            return 0;
        }

        try {
            DatabaseEntityPersister entityPersister = metaModel.getPersister(objects[0].getClass());
            DatabaseEntityMetadata<?> entityMetadata = entityPersister.getMetadata();
            EntityQuerySpace entityQuerySpace = entityPersister.getEntityQuerySpace();
            DatabaseColumnType idField = entityMetadata.getPrimaryKeyColumnType();
            CreateQuery createQuery = null;
            List<Map<Integer, Argument>> args = new ArrayList<>();

            for (Object object : objects) {
                Map<DatabaseColumnType, Argument> argumentMap = ejectForCreate(object, entityMetadata);

                if (createQuery == null) {
                    createQuery = entityQuerySpace.getCreatedQuery(argumentMap.keySet());
                }
                foreignAutoCreate(session, object, entityMetadata);
                args.add(new HashMap<Integer, Argument>() {{
                    AtomicInteger index = new AtomicInteger();

                    argumentMap.forEach((key, value) -> put(index.incrementAndGet(), value));
                }});
            }

            List<GeneratedKey> generatedKeys = new ArrayList<>();

            for (int i = 0; i < objects.length; ++i) {
                generatedKeys.add(new KeyHolder());
            }
            int result = databaseEngine.create(
                    session.getConnection(),
                    createQuery,
                    args,
                    idField,
                    generatedKeys
            );

            if (idField.generated()) {
                Iterator<GeneratedKey> generatedKeyIterator = generatedKeys.iterator();

                for (Object object : objects) {
                    processGeneratedKey(generatedKeyIterator.next(), idField, object);
                }
            } else {
                cacheHelper.saveToCache(objects, objects[0].getClass());
            }
            for (Object object : objects) {
                foreignAutoCreateForeignCollection(session, object, entityMetadata);
            }

            return result;
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * Process generated key.
     *
     * @param generatedKey target generated key
     * @param idColumnType target id column type
     * @param object       target object
     */
    private void processGeneratedKey(GeneratedKey generatedKey, DatabaseColumnType idColumnType, Object object) {
        if (generatedKey.get() != null) {
            idColumnType.assign(object, idColumnType.dataPersister().convertToPrimaryKey(generatedKey.get()));
            cacheHelper.saveToCache(generatedKey.get(), object);
        } else {
            throw new GeneratedValueNotFoundException(idColumnType.getField());
        }
    }

    @Override
    public boolean createTable(Session session, Class<?> tClass, boolean ifNotExist)
            throws SQLException {
        EntityQuerySpace entityQuerySpace = metaModel.getPersister(tClass).getEntityQuerySpace();
        CreateTableQuery createTableQuery = entityQuerySpace.createTableQuery(
                databaseEngine.getDialect(),
                ifNotExist
        );

        return databaseEngine.createTable(session.getConnection(), createTableQuery);
    }

    @Override
    public boolean dropTable(Session session, Class<?> tClass, boolean ifExist)
            throws SQLException {
        EntityQuerySpace entityQuerySpace = metaModel.getPersister(tClass).getEntityQuerySpace();
        DropTableQuery dropTableQuery = entityQuerySpace.getDropTableQuery(ifExist);

        return databaseEngine.dropTable(session.getConnection(), dropTableQuery);
    }

    @Override
    public int update(Session session, Object object) throws SQLException {
        DatabaseEntityPersister entityPersister = metaModel.getPersister(object.getClass());
        DatabaseEntityMetadata<?> entityMetadata = entityPersister.getMetadata();

        Map<DatabaseColumnType, Argument> arguments = ejectForUpdate(object, entityMetadata);
        UpdateQuery updateQuery = entityPersister.getEntityQuerySpace().getUpdateByIdQuery(arguments.keySet());
        DatabaseColumnType primaryKeyColumnType = entityMetadata.getPrimaryKeyColumnType();
        Argument id = eject(object, primaryKeyColumnType);

        arguments.put(primaryKeyColumnType, id);
        try {
            int result = databaseEngine.update(
                    session.getConnection(),
                    updateQuery,
                    new HashMap<Integer, Argument>() {{
                        AtomicInteger index = new AtomicInteger();

                        arguments.forEach((key, value) -> put(index.incrementAndGet(), value));
                    }}
            );

            cacheHelper.saveToCache(id.getValue(), object);

            return result;
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    @Override
    public int delete(Session session, Object object) throws SQLException {
        try {
            DatabaseEntityPersister entityPersister = metaModel.getPersister(object.getClass());
            Argument id = eject(object, entityPersister.getMetadata().getPrimaryKeyColumnType());

            DeleteQuery deleteQuery = entityPersister.getEntityQuerySpace().getDeleteByIdQuery();

            int result = databaseEngine.delete(
                    session.getConnection(),
                    deleteQuery,
                    new HashMap<Integer, Argument>() {{
                        put(1, id);
                    }}
            );

            cacheHelper.delete(object.getClass(), id.getValue());

            return result;
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    @Override
    public int deleteById(Session session, Class<?> tClass, Object id) throws SQLException {
        DatabaseEntityPersister entityPersister = metaModel.getPersister(tClass);
        DeleteQuery deleteQuery = entityPersister.getEntityQuerySpace().getDeleteByIdQuery();
        Argument idArgument = processConvertersToSqlValue(
                id,
                entityPersister.getMetadata().getPrimaryKeyColumnType()
        );

        int result = databaseEngine.delete(
                session.getConnection(),
                deleteQuery,
                new HashMap<Integer, Argument>() {{
                    put(1, idArgument);
                }}
        );

        cacheHelper.delete(tClass, id);

        return result;
    }

    @Override
    public <T> T queryForId(Session session, Class<T> tClass, Object id) throws SQLException {
        Optional<Object> instance = cacheHelper.get(tClass, id);

        if (instance.isPresent()) {
            return (T) instance.get();
        }

        DatabaseEntityPersister entityPersister = metaModel.getPersister(tClass);
        EntityQuerySpace entityQuerySpace = entityPersister.getEntityQuerySpace();
        Argument idArgument = processConvertersToSqlValue(
                id,
                entityPersister.getMetadata().getPrimaryKeyColumnType()
        );

        try (DatabaseResults databaseResults = databaseEngine.select(
                session.getConnection(),
                entityQuerySpace.getSelectById(),
                new HashMap<Integer, Argument>() {{
                    put(1, idArgument);
                }}
        )) {
            List<RowResult<T>> rowResults = entityPersister.load(session, databaseResults);

            if (!rowResults.isEmpty()) {
                RowResult<T> rowResult = rowResults.iterator().next();

                cacheHelper.saveToCache(rowResult.getId(), rowResult.getResult());

                return (T) rowResult.getResult();
            }
        }

        return null;
    }

    @Override
    public <T> List<T> queryForAll(Session session, Class<T> tClass) throws SQLException {
        DatabaseEntityPersister entityPersister = metaModel.getPersister(tClass);
        EntityQuerySpace entityQuerySpace = entityPersister.getEntityQuerySpace();

        try (DatabaseResults databaseResults = databaseEngine.select(
                session.getConnection(),
                entityQuerySpace.getSelectAll(),
                Collections.emptyMap()
        )) {
            List<RowResult<T>> results = entityPersister.load(session, databaseResults);

            for (RowResult<T> result : results) {
                cacheHelper.saveToCache(result.getId(), result.getResult());
            }

            return results.stream().map(RowResult::getResult).collect(Collectors.toList());
        }
    }

    @Override
    public boolean refresh(Session session, Object object) throws SQLException {
        DatabaseEntityPersister entityPersister = metaModel.getPersister(object.getClass());
        DatabaseColumnType primaryKeyType = entityPersister.getMetadata().getPrimaryKeyColumnType();
        EntityQuerySpace entityQuerySpace = entityPersister.getEntityQuerySpace();
        Argument idArgument = eject(object, primaryKeyType);

        try (DatabaseResults databaseResults = databaseEngine.select(
                session.getConnection(),
                entityQuerySpace.getSelectById(),
                new HashMap<Integer, Argument>() {{
                    put(1, idArgument);
                }}
        )) {
            List<RowResult<Object>> rowResults = entityPersister.load(session, databaseResults);

            if (!rowResults.isEmpty()) {
                RowResult<Object> rowResult = rowResults.iterator().next();
                Object newObject = rowResult.getResult();

                for (DatabaseColumnType columnType : entityPersister.getMetadata().getColumnTypes()) {
                    Object newValue = columnType.access(newObject);

                    columnType.assign(object, newValue);
                }
                cacheHelper.saveToCache(rowResult.getId(), object);

                return true;
            }
        }

        return false;
    }

    @Override
    public void createIndexes(Session session, Class<?> tClass) throws SQLException {
        DatabaseEntityPersister entityPersister = metaModel.getPersister(tClass);

        for (CreateIndexQuery createIndexQuery : entityPersister.getEntityQuerySpace().getCreateIndexQuery()) {
            databaseEngine.createIndex(session.getConnection(), createIndexQuery);
        }
    }

    @Override
    public void dropIndexes(Session session, Class<?> tClass) throws SQLException {
        DatabaseEntityPersister entityPersister = metaModel.getPersister(tClass);

        for (DropIndexQuery dropIndexQuery : entityPersister.getEntityQuerySpace().getDropIndexQuery()) {
            databaseEngine.dropIndex(session.getConnection(), dropIndexQuery);
        }
    }

    @Override
    public long countOff(Session session, Class<?> tClass) throws SQLException {
        DatabaseEntityPersister entityPersister = metaModel.getPersister(tClass);
        SelectQuery selectQuery = entityPersister.getEntityQuerySpace().countOff();

        try (DatabaseResults databaseResults = databaseEngine.select(
                session.getConnection(),
                selectQuery,
                new HashMap<>())
        ) {
            if (databaseResults.next()) {
                return databaseResults.getLong(1);
            }
        }

        return 0;
    }

    @Override
    public <T> List<T> list(Session session, SelectStatement<T> selectStatement) throws SQLException {
        DatabaseEntityPersister entityPersister = metaModel.getPersister(selectStatement.getEntityClass());
        SelectQuery selectQuery = entityPersister.getEntityQuerySpace().getBySelectStatement(selectStatement);

        try (DatabaseResults databaseResults = databaseEngine.select(
                session.getConnection(),
                selectQuery,
                getArguments(entityPersister.getMetadata(), selectStatement)
        )) {
            List<RowResult<T>> results = entityPersister.load(session, databaseResults);

            for (RowResult<T> result : results) {
                cacheHelper.saveToCache(result.getId(), result.getResult());
            }

            return results.stream().map(RowResult::getResult).collect(Collectors.toList());
        }
    }

    @Override
    public long queryForLong(Session session, SelectStatement<?> selectStatement)
            throws SQLException {
        DatabaseEntityPersister entityPersister = metaModel.getPersister(selectStatement.getEntityClass());
        SelectQuery selectQuery = entityPersister.getEntityQuerySpace().getSelectForLongResult(selectStatement);

        try (DatabaseResults databaseResults = databaseEngine.select(
                session.getConnection(),
                selectQuery,
                getArguments(entityPersister.getMetadata(), selectStatement)
        )) {
            if (databaseResults.next()) {
                return databaseResults.getLong(1);
            }
        }

        return 0;
    }

    @Override
    public DatabaseResults query(Session session, String query) throws SQLException {
        return databaseEngine.query(session.getConnection(), query);
    }

    @Override
    public int clearTable(Session session, Class<?> entityClass) throws SQLException {
        DatabaseEntityPersister entityPersister = metaModel.getPersister(entityClass);
        DeleteQuery clearQuery = entityPersister.getEntityQuerySpace().getDeleteAllQuery();

        return databaseEngine.delete(session.getConnection(), clearQuery, new HashMap<>());
    }

    /**
     * Foreign column
     * {@link ru.saidgadjiev.ormnext.core.field.ForeignColumn} object auto create if auto create enabled.
     *
     * @param session        target session
     * @param object         target object which contains foreign columns
     * @param entityMetadata target entity metadata
     * @throws SQLException any SQL exceptions
     */
    private void foreignAutoCreate(Session session,
                                   Object object,
                                   DatabaseEntityMetadata<?> entityMetadata
    ) throws SQLException {
        for (ForeignColumnTypeImpl foreignColumnType : entityMetadata.toForeignColumnTypes()) {
            Object foreignObject = foreignColumnType.access(object);

            if (foreignObject != null && foreignColumnType.foreignAutoCreate()) {
                session.create(foreignObject);
            }
        }
    }

    /**
     * Create objects from foreign collection field
     * {@link ru.saidgadjiev.ormnext.core.field.ForeignCollectionField} if enable auto create.
     *
     * @param session        target session
     * @param object         target object which contains foreign collection fields
     * @param entityMetadata target metadata.
     * @throws SQLException any SQL exceptions
     */
    private void foreignAutoCreateForeignCollection(Session session,
                                                    Object object,
                                                    DatabaseEntityMetadata<?> entityMetadata
    ) throws SQLException {
        for (ForeignCollectionColumnTypeImpl foreignCollectionColumnType
                : entityMetadata.toForeignCollectionColumnTypes()) {
            if (foreignCollectionColumnType.foreignAutoCreate()) {
                for (Object foreignObject : (Collection) foreignCollectionColumnType.access(object)) {
                    foreignCollectionColumnType.getForeignColumnType().assign(foreignObject, object);

                    session.create(foreignObject);
                }
            }
        }
    }

    /**
     * Obtain arguments {@link Argument} from criteria query.
     *
     * @param metadata        target table meta data
     * @param selectStatement target criteria query
     * @return argument index map
     */
    private Map<Integer, Argument> getArguments(DatabaseEntityMetadata<?> metadata,
                                                SelectStatement<?> selectStatement) {
        Map<Integer, Argument> args = new HashMap<>();
        AtomicInteger index = new AtomicInteger();

        for (CriterionArgument criterionArgument : selectStatement.getArgs()) {
            DatabaseColumnType columnType = DatabaseEntityMetadataUtils
                    .getDataTypeByPropertyName(metadata.getColumnTypes(), criterionArgument.getProperty())
                    .orElseThrow(() -> new PropertyNotFoundException(
                            metadata.getTableClass(),
                            criterionArgument.getProperty())
                    );

            for (Object arg : criterionArgument.getValues()) {
                args.put(index.incrementAndGet(), new Argument(columnType.dataType(), arg));
            }
        }

        for (Map.Entry<Integer, Object> entry : selectStatement.getUserProvidedArgs().entrySet()) {
            DataPersister dataPersister = DataPersisterManager.lookup(entry.getValue().getClass());

            args.put(entry.getKey(), new Argument(dataPersister.getDataType(), entry.getValue()));
        }

        return args;
    }

    /**
     * Generated key holder implementation.
     */
    private static class KeyHolder implements GeneratedKey {

        /**
         * Generated key.
         */
        private Object key;

        @Override
        public void set(Object key) throws SQLException {
            if (this.key != null) {
                throw new SQLException(
                        "Generated key has already been set to " + this.key + ", now set to " + key
                );
            }

            this.key = key;
        }

        @Override
        public Object get() {
            return key;
        }
    }
}
