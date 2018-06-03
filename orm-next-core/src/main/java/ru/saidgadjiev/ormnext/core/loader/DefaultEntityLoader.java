package ru.saidgadjiev.ormnext.core.loader;

import ru.saidgadjiev.ormnext.core.connectionsource.DatabaseConnection;
import ru.saidgadjiev.ormnext.core.connectionsource.DatabaseResults;
import ru.saidgadjiev.ormnext.core.dao.DatabaseEngine;
import ru.saidgadjiev.ormnext.core.dao.Session;
import ru.saidgadjiev.ormnext.core.exception.GeneratedValueNotFoundException;
import ru.saidgadjiev.ormnext.core.exception.PropertyNotFoundException;
import ru.saidgadjiev.ormnext.core.field.DataPersisterManager;
import ru.saidgadjiev.ormnext.core.field.datapersister.DataPersister;
import ru.saidgadjiev.ormnext.core.field.fieldtype.ForeignCollectionColumnType;
import ru.saidgadjiev.ormnext.core.field.fieldtype.ForeignColumnType;
import ru.saidgadjiev.ormnext.core.field.fieldtype.IDatabaseColumnType;
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
 * @author said gadjiev
 */
public class DefaultEntityLoader implements EntityLoader {

    /**
     * Session which create this instance.
     */
    private Session session;

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
     * Create a new instance.
     *
     * @param session        target session
     * @param metaModel      current meta model
     * @param databaseEngine target database engine
     */
    public DefaultEntityLoader(Session session, MetaModel metaModel, DatabaseEngine<?> databaseEngine) {
        this.session = session;
        this.databaseEngine = databaseEngine;
        this.metaModel = metaModel;
    }

    @Override
    public int create(DatabaseConnection connection, Object object) throws SQLException {
        try {
            DatabaseEntityPersister entityPersister = metaModel.getPersister(object.getClass());
            DatabaseEntityMetadata<?> entityMetadata = entityPersister.getMetadata();

            foreignAutoCreate(object, entityMetadata);

            Map<IDatabaseColumnType, Argument> argumentMap = ejectForCreate(object, entityMetadata);
            EntityQuerySpace entityQuerySpace = entityPersister.getEntityQuerySpace();
            CreateQuery createQuery = entityQuerySpace.getCreatedQuery(argumentMap.keySet());
            IDatabaseColumnType idField = entityMetadata.getPrimaryKeyColumnType();

            GeneratedKey generatedKey = new KeyHolder();

            int result = databaseEngine.create(connection, createQuery, new HashMap<Integer, Argument>() {{
                AtomicInteger index = new AtomicInteger();

                argumentMap.forEach((key, value) -> put(index.incrementAndGet(), value));
            }}, idField, generatedKey);

            if (idField.isGenerated()) {
                if (generatedKey.get() != null) {
                    idField.assign(object, generatedKey.get());
                    session.cacheHelper().saveToCache(generatedKey.get(), object);
                } else {
                    throw new GeneratedValueNotFoundException(idField.getField());
                }
            } else {
                session.cacheHelper().saveToCache(null, object);
            }

            foreignAutoCreateForeignCollectionColumnType(object, entityMetadata);

            return result;
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    @Override
    public int create(DatabaseConnection connection, Object[] objects) throws SQLException {
        if (objects.length == 0) {
            return 0;
        }

        try {
            DatabaseEntityPersister entityPersister = metaModel.getPersister(objects[0].getClass());
            EntityQuerySpace entityQuerySpace = entityPersister.getEntityQuerySpace();
            IDatabaseColumnType idField = entityPersister.getMetadata().getPrimaryKeyColumnType();
            CreateQuery createQuery = null;
            List<Map<Integer, Argument>> args = new ArrayList<>();

            for (Object object : objects) {
                Map<IDatabaseColumnType, Argument> argumentMap = ejectForCreate(object, entityPersister.getMetadata());

                if (createQuery == null) {
                    createQuery = entityQuerySpace.getCreatedQuery(argumentMap.keySet());
                }
                foreignAutoCreate(object, entityPersister.getMetadata());
                args.add(new HashMap<Integer, Argument>() {{
                    AtomicInteger index = new AtomicInteger();

                    argumentMap.forEach((key, value) -> put(index.incrementAndGet(), value));
                }});
            }

            List<GeneratedKey> generatedKeys = new ArrayList<>();

            for (int i = 0; i < objects.length; ++i) {
                generatedKeys.add(new KeyHolder());
            }
            int result = databaseEngine.create(connection, createQuery, args, idField, generatedKeys);

            if (idField.isGenerated()) {
                Iterator<GeneratedKey> generatedKeyIterator = generatedKeys.iterator();

                for (Object object : objects) {
                    GeneratedKey generatedKey = generatedKeyIterator.next();

                    if (generatedKey.get() != null) {
                        idField.assign(object, generatedKey.get());
                        session.cacheHelper().saveToCache(generatedKey.get(), object);
                    } else {
                        throw new GeneratedValueNotFoundException(idField.getField());
                    }
                }
            } else {
                session.cacheHelper().saveToCache(objects, objects[0].getClass());
            }
            for (Object object : objects) {
                foreignAutoCreateForeignCollectionColumnType(object, entityPersister.getMetadata());
            }

            return result;
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    @Override
    public boolean createTable(DatabaseConnection connection, Class<?> tClass, boolean ifNotExist)
            throws SQLException {
        EntityQuerySpace entityQuerySpace = metaModel.getPersister(tClass).getEntityQuerySpace();
        CreateTableQuery createTableQuery = entityQuerySpace.createTableQuery(
                databaseEngine.getDatabaseType(),
                ifNotExist
        );

        return databaseEngine.createTable(connection, createTableQuery);
    }

    @Override
    public boolean dropTable(DatabaseConnection connection, Class<?> tClass, boolean ifExist)
            throws SQLException {
        EntityQuerySpace entityQuerySpace = metaModel.getPersister(tClass).getEntityQuerySpace();
        DropTableQuery dropTableQuery = entityQuerySpace.getDropTableQuery(ifExist);

        return databaseEngine.dropTable(connection, dropTableQuery);
    }

    @Override
    public int update(DatabaseConnection connection, Object object) throws SQLException {
        DatabaseEntityPersister entityPersister = metaModel.getPersister(object.getClass());
        DatabaseEntityMetadata<?> entityMetadata = entityPersister.getMetadata();

        Map<IDatabaseColumnType, Argument> arguments = ejectForUpdate(object, entityMetadata);
        UpdateQuery updateQuery = entityPersister.getEntityQuerySpace().getUpdateByIdQuery(arguments.keySet());
        IDatabaseColumnType primaryKeyColumnType = entityMetadata.getPrimaryKeyColumnType();
        Argument id = eject(object, primaryKeyColumnType);

        arguments.put(primaryKeyColumnType, id);
        try {
            int result = databaseEngine.update(connection, updateQuery, new HashMap<Integer, Argument>() {{
                AtomicInteger index = new AtomicInteger();

                arguments.forEach((key, value) -> put(index.incrementAndGet(), value));
            }});

            session.cacheHelper().saveToCache(id.getValue(), object);

            return result;
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    @Override
    public int delete(DatabaseConnection connection, Object object) throws SQLException {
        try {
            DatabaseEntityPersister entityPersister = metaModel.getPersister(object.getClass());
            Argument id = eject(object, entityPersister.getMetadata().getPrimaryKeyColumnType());

            DeleteQuery deleteQuery = entityPersister.getEntityQuerySpace().getDeleteByIdQuery();

            int result = databaseEngine.delete(connection, deleteQuery, new HashMap<Integer, Argument>() {{
                put(1, id);
            }});

            session.cacheHelper().delete(object.getClass(), id.getValue());

            return result;
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    @Override
    public int deleteById(DatabaseConnection connection, Class<?> tClass, Object id) throws SQLException {
        DatabaseEntityPersister entityPersister = metaModel.getPersister(tClass);
        DeleteQuery deleteQuery = entityPersister.getEntityQuerySpace().getDeleteByIdQuery();
        Argument idArgument = processConvertersToSqlValue(
                id,
                entityPersister.getMetadata().getPrimaryKeyColumnType()
        );

        int result = databaseEngine.delete(connection, deleteQuery, new HashMap<Integer, Argument>() {{
            put(1, idArgument);
        }});

        session.cacheHelper().delete(tClass, id);

        return result;
    }

    @Override
    public <T> T queryForId(DatabaseConnection connection, Class<T> tClass, Object id) throws SQLException {
        Optional<Object> instance = session.cacheHelper().get(tClass, id);

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
                connection,
                entityQuerySpace.getSelectById(),
                new HashMap<Integer, Argument>() {{
                    put(1, idArgument);
                }}
        )) {
            List<RowResult<Object>> rowResults = entityPersister.load(session, databaseResults);

            if (!rowResults.isEmpty()) {
                RowResult<Object> rowResult = rowResults.iterator().next();

                session.cacheHelper().saveToCache(rowResult.getId(), rowResult.getResult());

                return (T) rowResult.getResult();
            }
        }

        return null;
    }

    @Override
    public <T> List<T> queryForAll(DatabaseConnection connection, Class<T> tClass) throws SQLException {
        DatabaseEntityPersister entityPersister = metaModel.getPersister(tClass);
        EntityQuerySpace entityQuerySpace = entityPersister.getEntityQuerySpace();

        try (DatabaseResults databaseResults = databaseEngine.select(
                connection,
                entityQuerySpace.getSelectAll(),
                Collections.emptyMap()
        )) {
            List<RowResult<Object>> results = entityPersister.load(session, databaseResults);

            for (RowResult<Object> result : results) {
                session.cacheHelper().saveToCache(result.getId(), result.getResult());
            }

            return (List<T>) results.stream().map(RowResult::getResult).collect(Collectors.toList());
        }
    }

    @Override
    public boolean refresh(DatabaseConnection databaseConnection, Object object) throws SQLException {
        DatabaseEntityPersister entityPersister = metaModel.getPersister(object.getClass());
        IDatabaseColumnType primaryKeyType = entityPersister.getMetadata().getPrimaryKeyColumnType();
        EntityQuerySpace entityQuerySpace = entityPersister.getEntityQuerySpace();
        Argument idArgument = eject(object, primaryKeyType);

        try (DatabaseResults databaseResults = databaseEngine.select(
                databaseConnection,
                entityQuerySpace.getSelectById(),
                new HashMap<Integer, Argument>() {{
                    put(1, idArgument);
                }}
        )) {
            List<RowResult<Object>> rowResults = entityPersister.load(session, databaseResults);

            if (!rowResults.isEmpty()) {
                RowResult<Object> rowResult = rowResults.iterator().next();
                Object newObject = rowResult.getResult();

                for (IDatabaseColumnType columnType : entityPersister.getMetadata().getColumnTypes()) {
                    Object newValue = columnType.access(newObject);

                    columnType.assign(object, newValue);
                }
                session.cacheHelper().saveToCache(rowResult.getId(), object);

                return true;
            }
        }

        return false;
    }

    @Override
    public void createIndexes(DatabaseConnection connection, Class<?> tClass) throws SQLException {
        DatabaseEntityPersister entityPersister = metaModel.getPersister(tClass);

        for (CreateIndexQuery createIndexQuery : entityPersister.getEntityQuerySpace().getCreateIndexQuery()) {
            databaseEngine.createIndex(connection, createIndexQuery);
        }
    }

    @Override
    public void dropIndexes(DatabaseConnection connection, Class<?> tClass) throws SQLException {
        DatabaseEntityPersister entityPersister = metaModel.getPersister(tClass);

        for (DropIndexQuery dropIndexQuery : entityPersister.getEntityQuerySpace().getDropIndexQuery()) {
            databaseEngine.dropIndex(connection, dropIndexQuery);
        }
    }

    @Override
    public long countOff(DatabaseConnection connection, Class<?> tClass) throws SQLException {
        DatabaseEntityPersister entityPersister = metaModel.getPersister(tClass);
        SelectQuery selectQuery = entityPersister.getEntityQuerySpace().countOff();

        try (DatabaseResults databaseResults = databaseEngine.select(connection, selectQuery, new HashMap<>())) {
            if (databaseResults.next()) {
                return databaseResults.getLong(1);
            }
        }

        return 0;
    }

    @Override
    public <T> List<T> list(DatabaseConnection connection, SelectStatement<T> selectStatement) throws SQLException {
        DatabaseEntityPersister entityPersister = metaModel.getPersister(selectStatement.getEntityClass());
        IDatabaseColumnType primaryKeyType = entityPersister.getMetadata().getPrimaryKeyColumnType();
        SelectQuery selectQuery = entityPersister.getEntityQuerySpace().getBySelectStatement(selectStatement);

        try (DatabaseResults databaseResults = databaseEngine.select(
                connection,
                selectQuery,
                getArguments(entityPersister.getMetadata(), selectStatement)
        )) {
            List<T> results = (List<T>) entityPersister.load(session, databaseResults);

            for (T result : results) {
                session.cacheHelper().saveToCache(primaryKeyType.access(result), result);
            }

            return results;
        }
    }

    @Override
    public long queryForLong(DatabaseConnection connection, SelectStatement<?> selectStatement)
            throws SQLException {
        DatabaseEntityPersister entityPersister = metaModel.getPersister(selectStatement.getEntityClass());
        SelectQuery selectQuery = entityPersister.getEntityQuerySpace().getSelectForLongResult(selectStatement);

        try (DatabaseResults databaseResults = databaseEngine.select(
                connection,
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
    public DatabaseResults query(DatabaseConnection databaseConnection, String query) throws SQLException {
        return databaseEngine.query(databaseConnection, query);
    }

    private void foreignAutoCreate(Object object, DatabaseEntityMetadata<?> entityMetadata) throws SQLException {
        for (ForeignColumnType foreignColumnType : entityMetadata.toForeignColumnTypes()) {
            Object foreignObject = foreignColumnType.access(object);

            if (foreignObject != null && foreignColumnType.foreignAutoCreate()) {
                session.create(foreignObject);
            }
        }
    }

    private void foreignAutoCreateForeignCollectionColumnType(Object object, DatabaseEntityMetadata<?> entityMetadata) throws SQLException {
        for (ForeignCollectionColumnType foreignCollectionColumnType : entityMetadata.toForeignCollectionColumnTypes()) {
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
            IDatabaseColumnType columnType = DatabaseEntityMetadataUtils
                    .getDataTypeByPropertyName(metadata.getColumnTypes(), criterionArgument.getProperty())
                    .orElseThrow(() -> new PropertyNotFoundException(
                            metadata.getTableClass(),
                            criterionArgument.getProperty())
                    );

            for (Object arg : criterionArgument.getValues()) {
                args.put(index.incrementAndGet(), new Argument(columnType.getDataType(), arg));
            }
        }

        for (Map.Entry<Integer, Object> entry : selectStatement.getUserProvidedArgs().entrySet()) {
            DataPersister dataPersister = DataPersisterManager.lookup(entry.getValue().getClass());

            args.put(entry.getKey(), new Argument(dataPersister.getDataType(), entry.getValue()));
        }

        return args;
    }

    private static class KeyHolder implements GeneratedKey {

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
