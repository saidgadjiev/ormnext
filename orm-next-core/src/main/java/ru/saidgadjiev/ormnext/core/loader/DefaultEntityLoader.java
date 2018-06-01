package ru.saidgadjiev.ormnext.core.loader;

import ru.saidgadjiev.ormnext.core.connectionsource.DatabaseConnection;
import ru.saidgadjiev.ormnext.core.connectionsource.DatabaseResults;
import ru.saidgadjiev.ormnext.core.dao.DatabaseEngine;
import ru.saidgadjiev.ormnext.core.dao.Session;
import ru.saidgadjiev.ormnext.core.exception.GeneratedValueNotFoundException;
import ru.saidgadjiev.ormnext.core.exception.PropertyNotFoundException;
import ru.saidgadjiev.ormnext.core.field.DataPersisterManager;
import ru.saidgadjiev.ormnext.core.field.datapersister.DataPersister;
import ru.saidgadjiev.ormnext.core.field.fieldtype.ForeignColumnType;
import ru.saidgadjiev.ormnext.core.field.fieldtype.IDatabaseColumnType;
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
            Map<IDatabaseColumnType, Argument> argumentMap = ejectForCreate(object, entityPersister.getMetadata());
            EntityQuerySpace entityQuerySpace = entityPersister.getEntityQuerySpace();
            CreateQuery createQuery = entityQuerySpace.getCreateQuery(argumentMap.keySet());
            IDatabaseColumnType idField = entityPersister.getMetadata().getPrimaryKeyColumnType();

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

        UpdateQuery updateQuery = entityPersister.getEntityQuerySpace().getUpdateQuery(updatableColumnTypes);
        Map<IDatabaseColumnType, Argument> arguments = ejectForUpdate(object, entityMetadata);

        try {
            databaseEngine.update(connection, updateQuery, new HashMap<Integer, Argument>() {{
                AtomicInteger index = new AtomicInteger();

                arguments.forEach((key, value) -> put(index.incrementAndGet(), value));
            }});
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
            Argument id = eject(object, entityPersister.getMetadata().getPrimaryKeyColumnType());

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
        Argument idArgument = processConvertersToSqlValue(
                id,
                entityPersister.getMetadata().getPrimaryKeyColumnType()
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
     * Return all objects by select query.
     *
     * @param connection    target connection
     * @param selectStatement target query
     * @param <T>           table type
     * @return objects list
     * @throws SQLException any SQL exceptions
     */
    public <T> List<T> list(DatabaseConnection connection, SelectStatement<?> selectStatement) throws SQLException {
        DatabaseEntityPersister entityPersister = metaModel.getPersister(selectStatement.getEntityClass());
        Select select = entityPersister.getEntityQuerySpace().getBySelectStatement(selectStatement);

        try (DatabaseResults databaseResults = databaseEngine.select(
                connection,
                select,
                getArguments(entityPersister.getMetadata(), selectStatement)
        )) {
            return (List<T>) entityPersister.load(session, databaseResults);
        }
    }

    /**
     * Return long result by select query.
     *
     * @param connection    target connection
     * @param selectStatement target query
     * @param <T>           table type
     * @return result long value
     * @throws SQLException any SQL exceptions
     */
    public <T> long queryForLong(DatabaseConnection connection, SelectStatement<T> selectStatement)
            throws SQLException {
        DatabaseEntityPersister entityPersister = metaModel.getPersister(selectStatement.getEntityClass());
        Select select = entityPersister.getEntityQuerySpace().getSelectForLongResult(selectStatement);

        try (DatabaseResults databaseResults = databaseEngine.select(
                connection,
                select,
                getArguments(entityPersister.getMetadata(), selectStatement)
        )) {
            if (databaseResults.next()) {
                return databaseResults.getLong(1);
            }
        }

        return 0;
    }

    /**
     * Execute query by database engine and return results.
     *
     * @param databaseConnection target connection
     * @param query              target query
     * @return database results
     * @throws SQLException any SQL exceptions
     */
    public DatabaseResults query(DatabaseConnection databaseConnection, String query) throws SQLException {
        return databaseEngine.query(databaseConnection, query);
    }

    /**
     * Obtain arguments {@link Argument} from criteria query.
     *
     * @param metadata      target table meta data
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

}
