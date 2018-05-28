package ru.saidgadjiev.ormnext.core.loader;

import ru.saidgadjiev.ormnext.core.criteria.impl.CriteriaQuery;
import ru.saidgadjiev.ormnext.core.criteria.impl.CriterionArgument;
import ru.saidgadjiev.ormnext.core.dao.DatabaseEngine;
import ru.saidgadjiev.ormnext.core.dao.Session;
import ru.saidgadjiev.ormnext.core.exception.GeneratedValueNotFoundException;
import ru.saidgadjiev.ormnext.core.exception.PropertyNotFoundException;
import ru.saidgadjiev.ormnext.core.field.DataPersisterManager;
import ru.saidgadjiev.ormnext.core.field.field_type.ForeignColumnType;
import ru.saidgadjiev.ormnext.core.field.field_type.IDatabaseColumnType;
import ru.saidgadjiev.ormnext.core.field.data_persister.DataPersister;
import ru.saidgadjiev.ormnext.core.query.core.*;
import ru.saidgadjiev.ormnext.core.connection_source.DatabaseConnection;
import ru.saidgadjiev.ormnext.core.connection_source.DatabaseResults;
import ru.saidgadjiev.ormnext.core.query_element.*;
import ru.saidgadjiev.ormnext.core.table.internal.metamodel.DatabaseEntityMetadata;
import ru.saidgadjiev.ormnext.core.table.internal.metamodel.MetaModel;
import ru.saidgadjiev.ormnext.core.table.internal.persister.DatabaseEntityPersister;
import ru.saidgadjiev.ormnext.core.table.internal.query_space.EntityQuerySpace;
import ru.saidgadjiev.ormnext.core.utils.ArgumentUtils;
import ru.saidgadjiev.ormnext.core.utils.DatabaseEntityMetadataUtils;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Executes SQL statements via {@link DatabaseEngine}.
 */
public class DefaultEntityLoader {

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

    /**
     * Create a new entry in the database from an object.
     *
     * @param connection target connection
     * @param object     target object
     * @param <T>        object type
     * @throws SQLException any SQL exceptions
     */
    public <T> void create(DatabaseConnection connection, T object) throws SQLException {
        try {
            DatabaseEntityPersister entityPersister = metaModel.getPersister(object.getClass());
            Map<IDatabaseColumnType, Argument> argumentMap = ArgumentUtils.eject(object, entityPersister.getMetadata());
            EntityQuerySpace entityQuerySpace = entityPersister.getEntityQuerySpace();
            CreateQuery createQuery = entityQuerySpace.getCreateQuery(argumentMap.keySet());
            IDatabaseColumnType idField = entityPersister.getMetadata().getPrimaryKey();

            for (ForeignColumnType fieldType : entityPersister.getMetadata().toForeignColumnTypes()) {
                Object foreignObject = fieldType.access(object);

                if (foreignObject != null && fieldType.isForeignAutoCreate()) {
                    session.create(foreignObject);
                }
            }
            GeneratedKey generatedKey = new GeneratedKey() {

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
            };
            databaseEngine.create(connection, createQuery, new HashMap<Integer, Argument>() {{
                AtomicInteger index = new AtomicInteger();

                argumentMap.forEach((key, value) -> put(index.incrementAndGet(), value));
            }}, idField, generatedKey);

            if (idField.isGenerated()) {
                if (generatedKey.get() != null) {
                    idField.assign(object, generatedKey.get());
                } else {
                    throw new GeneratedValueNotFoundException(idField.getField());
                }

            }
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * Create table in the database.
     *
     * @param connection target connection
     * @param tClass     table class
     * @param ifNotExist append if not exist
     * @param <T>        table type
     * @return true if table create success
     * @throws SQLException any SQL exceptions
     */
    public <T> boolean createTable(DatabaseConnection connection, Class<T> tClass, boolean ifNotExist)
            throws SQLException {
        EntityQuerySpace entityQuerySpace = metaModel.getPersister(tClass).getEntityQuerySpace();
        CreateTableQuery createTableQuery = entityQuerySpace.createTableQuery(ifNotExist);

        return databaseEngine.createTable(connection, createTableQuery);
    }

    /**
     * Drop table from the database.
     *
     * @param connection target connection
     * @param tClass     table class
     * @param ifExist    append if exist
     * @param <T>        table type
     * @return true if table drop success
     * @throws SQLException any SQL exceptions
     */
    public <T> boolean dropTable(DatabaseConnection connection, Class<T> tClass, boolean ifExist)
            throws SQLException {
        EntityQuerySpace entityQuerySpace = metaModel.getPersister(tClass).getEntityQuerySpace();
        DropTableQuery dropTableQuery = entityQuerySpace.getDropTableQuery(ifExist);

        return databaseEngine.dropTable(connection, dropTableQuery);
    }

    /**
     * Update an object in the database.
     *
     * @param connection target connection
     * @param object     target object
     * @param <T>        object type
     * @throws SQLException any SQL exceptions
     */
    public <T> void update(DatabaseConnection connection, T object) throws SQLException {
        DatabaseEntityPersister entityPersister = metaModel.getPersister(object.getClass());
        DatabaseEntityMetadata<?> entityMetadata = entityPersister.getMetadata();

        List<IDatabaseColumnType> updatableColumnTypes = entityMetadata.getColumnTypes()
                .stream()
                .filter(IDatabaseColumnType::updatable)
                .collect(Collectors.toList());

        IDatabaseColumnType idFieldType = entityPersister.getMetadata().getPrimaryKey();
        UpdateQuery updateQuery = entityPersister.getEntityQuerySpace().getUpdateQuery(updatableColumnTypes);

        try {
            Map<Integer, Argument> args = new HashMap<>();
            AtomicInteger index = new AtomicInteger();

            for (IDatabaseColumnType databaseColumnType : entityPersister.getMetadata().toDatabaseColumnTypes()) {
                args.put(index.incrementAndGet(), ArgumentUtils.eject(object, databaseColumnType));
            }
            args.put(index.incrementAndGet(), new Argument(idFieldType.getDataType(), idFieldType.access(object)));

            databaseEngine.update(connection, updateQuery, args);
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * Delete an object from the database.
     *
     * @param connection target connection
     * @param object     target object
     * @param <T>        object type
     * @throws SQLException any SQL exceptions
     */
    public <T> void delete(DatabaseConnection connection, T object) throws SQLException {
        try {
            DatabaseEntityPersister entityPersister = metaModel.getPersister(object.getClass());
            Argument id = ArgumentUtils.eject(object, entityPersister.getMetadata().getPrimaryKey());

            DeleteQuery deleteQuery = entityPersister.getEntityQuerySpace().getDeleteQuery();

            databaseEngine.delete(connection, deleteQuery, new HashMap<Integer, Argument>() {{
                put(1, id);
            }});
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * Delete an object in the database by id.
     *
     * @param connection target connection
     * @param tClass     target object class
     * @param id         target id
     * @param <T>        object type
     * @param <ID>       id type
     * @throws SQLException any SQL exceptions
     */
    public <T, ID> void deleteById(DatabaseConnection connection, Class<T> tClass, ID id) throws SQLException {
        DatabaseEntityPersister entityPersister = metaModel.getPersister(tClass);
        DeleteQuery deleteQuery = entityPersister.getEntityQuerySpace().getDeleteQuery();
        Argument idArgument = ArgumentUtils.processConvertersToSqlValue(
                id,
                entityPersister.getMetadata().getPrimaryKey()
        );

        databaseEngine.delete(connection, deleteQuery, new HashMap<Integer, Argument>() {{
            put(1, idArgument);
        }});
    }

    /**
     * Return the object associated with the id or null if none.
     *
     * @param connection target connection
     * @param tClass     result object class
     * @param id         target id
     * @param <T>        result object type
     * @param <ID>       target id type
     * @return object associated with requested id
     * @throws SQLException any SQL exceptions
     */
    public <T, ID> T queryForId(DatabaseConnection connection, Class<T> tClass, ID id) throws SQLException {
        Optional<Object> instance = session.cacheHelper().get(tClass, id);

        if (instance.isPresent()) {
            return (T) instance.get();
        }

        DatabaseEntityPersister entityPersister = metaModel.getPersister(tClass);
        EntityQuerySpace entityQuerySpace = entityPersister.getEntityQuerySpace();
        Argument idArgument = ArgumentUtils.processConvertersToSqlValue(
                id,
                entityPersister.getMetadata().getPrimaryKey()
        );

        try (DatabaseResults databaseResults = databaseEngine.select(
                connection,
                entityQuerySpace.getSelectById(),
                new HashMap<Integer, Argument>() {{
                    put(1, idArgument);
                }}
        )) {
            List<Object> result = entityPersister.load(session, databaseResults);

            if (!result.isEmpty()) {
                result.iterator().next();
            }
        }

        return null;
    }

    /**
     * Return all objects from table.
     *
     * @param connection target connection
     * @param tClass     result table class
     * @param <T>        table type
     * @return all objects from requested table
     * @throws SQLException any SQL exceptions
     */
    public <T> List<T> queryForAll(DatabaseConnection connection, Class<T> tClass) throws SQLException {
        DatabaseEntityPersister entityPersister = metaModel.getPersister(tClass);
        EntityQuerySpace entityQuerySpace = entityPersister.getEntityQuerySpace();

        try (DatabaseResults databaseResults = databaseEngine.select(
                connection,
                entityQuerySpace.getSelectAll(),
                Collections.emptyMap()
        )) {
            return (List<T>) entityPersister.load(session, databaseResults);
        }
    }

    /**
     * Create table indexes.
     *
     * @param connection target connection
     * @param tClass     table class
     * @param <T>        table type
     * @throws SQLException any SQL exceptions
     */
    public <T> void createIndexes(DatabaseConnection connection, Class<T> tClass) throws SQLException {
        DatabaseEntityPersister entityPersister = metaModel.getPersister(tClass);

        for (CreateIndexQuery createIndexQuery : entityPersister.getEntityQuerySpace().getCreateIndexQuery()) {
            databaseEngine.createIndex(connection, createIndexQuery);
        }
    }

    /**
     * Drop table indexes.
     *
     * @param connection target connection
     * @param tClass     table class
     * @param <T>        table type
     * @throws SQLException any SQL exceptions
     */
    public <T> void dropIndexes(DatabaseConnection connection, Class<T> tClass) throws SQLException {
        DatabaseEntityPersister entityPersister = metaModel.getPersister(tClass);

        for (DropIndexQuery dropIndexQuery : entityPersister.getEntityQuerySpace().getDropIndexQuery()) {
            databaseEngine.dropIndex(connection, dropIndexQuery);
        }
    }

    /**
     * Count star in the table.
     *
     * @param connection target connection
     * @param tClass     table class
     * @param <T>        table type
     * @return count off entities in the table
     * @throws SQLException any SQL exceptions
     */
    public <T> long countOff(DatabaseConnection connection, Class<T> tClass) throws SQLException {
        DatabaseEntityPersister entityPersister = metaModel.getPersister(tClass);
        Select select = entityPersister.getEntityQuerySpace().countOff();

        try (DatabaseResults databaseResults = databaseEngine.select(connection, select, new HashMap<>())) {
            if (databaseResults.next()) {
                return databaseResults.getLong(1);
            }
        }

        return 0;
    }

    /**
     * Return all objects by criteria query.
     *
     * @param connection    target connection
     * @param criteriaQuery target query
     * @param <T>           table type
     * @return objects list
     * @throws SQLException any SQL exceptions
     */
    public <T> List<T> list(DatabaseConnection connection, CriteriaQuery<?> criteriaQuery) throws SQLException {
        DatabaseEntityPersister entityPersister = metaModel.getPersister(criteriaQuery.getEntityClass());
        Select select = entityPersister.getEntityQuerySpace().getByCriteria(criteriaQuery);

        try (DatabaseResults databaseResults = databaseEngine.select(
                connection,
                select,
                getArguments(entityPersister.getMetadata(), criteriaQuery)
        )) {
            return (List<T>) entityPersister.load(session, databaseResults);
        }
    }

    /**
     * Return long result by criteria query.
     *
     * @param connection    target connection
     * @param criteriaQuery target query
     * @param <T>           table type
     * @return result long value
     * @throws SQLException any SQL exceptions
     */
    public <T> long queryForLong(DatabaseConnection connection, CriteriaQuery<T> criteriaQuery) throws SQLException {
        DatabaseEntityPersister entityPersister = metaModel.getPersister(criteriaQuery.getEntityClass());
        Select select = entityPersister.getEntityQuerySpace().getByCriteriaForLongResult(criteriaQuery);

        try (DatabaseResults databaseResults = databaseEngine.select(
                connection,
                select,
                getArguments(entityPersister.getMetadata(), criteriaQuery)
        )) {
            if (databaseResults.next()) {
                return databaseResults.getLong(1);
            }
        }

        return 0;
    }

    /**
     * Obtain arguments {@link Argument} from criteria query.
     *
     * @param metadata      target table meta data
     * @param criteriaQuery target criteria query
     * @return argument index map
     */
    private Map<Integer, Argument> getArguments(DatabaseEntityMetadata<?> metadata, CriteriaQuery<?> criteriaQuery) {
        Map<Integer, Argument> args = new HashMap<>();
        AtomicInteger index = new AtomicInteger();

        for (CriterionArgument criterionArgument : criteriaQuery.getArgs()) {
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

        for (Map.Entry<Integer, Object> entry : criteriaQuery.getUserProvidedArgs().entrySet()) {
            DataPersister dataPersister = DataPersisterManager.lookup(entry.getValue().getClass());

            args.put(entry.getKey(), new Argument(dataPersister.getDataType(), entry.getValue()));
        }

        return args;
    }

}
