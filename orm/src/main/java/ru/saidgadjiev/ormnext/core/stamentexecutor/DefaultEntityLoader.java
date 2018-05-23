package ru.saidgadjiev.ormnext.core.stamentexecutor;

import ru.saidgadjiev.ormnext.core.criteria.impl.CriteriaQuery;
import ru.saidgadjiev.ormnext.core.criteria.impl.CriterionArgument;
import ru.saidgadjiev.ormnext.core.dao.DatabaseEngine;
import ru.saidgadjiev.ormnext.core.dao.Session;
import ru.saidgadjiev.ormnext.core.exception.GeneratedValueNotFoundException;
import ru.saidgadjiev.ormnext.core.field.DataPersisterManager;
import ru.saidgadjiev.ormnext.core.field.fieldtype.ForeignColumnType;
import ru.saidgadjiev.ormnext.core.field.fieldtype.IDatabaseColumnType;
import ru.saidgadjiev.ormnext.core.field.persister.Converter;
import ru.saidgadjiev.ormnext.core.field.persister.DataPersister;
import ru.saidgadjiev.ormnext.core.query.core.*;
import ru.saidgadjiev.ormnext.core.support.DatabaseConnection;
import ru.saidgadjiev.ormnext.core.support.DatabaseResults;
import ru.saidgadjiev.ormnext.core.table.internal.metamodel.DatabaseEntityMetadata;
import ru.saidgadjiev.ormnext.core.table.internal.metamodel.MetaModel;
import ru.saidgadjiev.ormnext.core.table.internal.persister.DatabaseEntityPersister;
import ru.saidgadjiev.ormnext.core.utils.ArgumentUtils;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Класс для выполнения sql запросов
 */
@SuppressWarnings("PMD")
public class DefaultEntityLoader {

    private Session dao;

    private MetaModel metaModel;

    private DatabaseEngine databaseEngine;

    public DefaultEntityLoader(Session dao, MetaModel metaModel, DatabaseEngine databaseEngine) {
        this.dao = dao;
        this.databaseEngine = databaseEngine;
        this.metaModel = metaModel;
    }

    /**
     * Сохраняет объект в базе
     * Выполняет запрос вида INSERT INTO ...(colname1, colname2, ...) VALUES(colvalue1, colvalue2, ...)
     */
    @SuppressWarnings("unchecked")
    public <T> void create(DatabaseConnection connection, T object) throws SQLException {
        try {
            DatabaseEntityPersister entityPersister = metaModel.getPersister(object.getClass());
            Map<IDatabaseColumnType, Argument> argumentMap = ArgumentUtils.eject(object, entityPersister.getMetadata());
            CreateQuery createQuery = entityPersister.getEntityQuerySpace().getCreateQuery(argumentMap.keySet());
            IDatabaseColumnType idField = entityPersister.getMetadata().getPrimaryKey();

            for (ForeignColumnType fieldType : entityPersister.getMetadata().toForeignFieldTypes()) {
                Object foreignObject = fieldType.access(object);

                if (foreignObject != null && fieldType.isForeignAutoCreate()) {
                    dao.create(foreignObject);
                }
            }
            try (DatabaseResults databaseResults = databaseEngine.create(connection, createQuery, new HashMap<Integer, Argument>() {{
                AtomicInteger index = new AtomicInteger();

                argumentMap.forEach((key, value) -> put(index.incrementAndGet(), value));
            }})) {
                if (idField.isGenerated()) {
                    if (databaseResults.next()) {
                        idField.assign(object, idField.getDataPersister().readValue(databaseResults, 1));
                    } else {
                        throw new GeneratedValueNotFoundException(idField.getField());
                    }
                }
            }
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    public <T> boolean createTable(DatabaseConnection connection, Class<T> tClass, boolean ifNotExists) throws SQLException {
        CreateTableQuery createTableQuery = metaModel.getPersister(tClass).getEntityQuerySpace().createTableQuery(ifNotExists);

        return databaseEngine.createTable(connection, createTableQuery);
    }

    public <T> boolean dropTable(DatabaseConnection connection, Class<T> tClass, boolean ifExists) throws SQLException {
        DropTableQuery dropTableQuery = metaModel.getPersister(tClass).getEntityQuerySpace().getDropTableQuery(ifExists);

        return databaseEngine.dropTable(connection, dropTableQuery);
    }

    /**
     * Обновляет объект в базе
     * Выполняет запрос вида UPDATE ... SET colname1 = colvalue1 SET colname2 = colvalue2 WHERE = object_builder.id
     */
    public <T> void update(DatabaseConnection connection, T object) throws SQLException {
        DatabaseEntityPersister entityPersister = metaModel.getPersister(object.getClass());
        DatabaseEntityMetadata<?> entityMetadata = entityPersister.getMetadata();
        List<IDatabaseColumnType> updatableColumnTypes = entityMetadata.getFieldTypes().stream().filter(IDatabaseColumnType::updatable).collect(Collectors.toList());
        IDatabaseColumnType idFieldType = entityPersister.getMetadata().getPrimaryKey();
        UpdateQuery updateQuery = entityPersister.getEntityQuerySpace().getUpdateQuery(updatableColumnTypes);

        try {
            Map<Integer, Argument> args = new HashMap<>();
            AtomicInteger index = new AtomicInteger();

            for (IDatabaseColumnType databaseColumnType : entityPersister.getMetadata().toDBFieldTypes()) {
                Object value = databaseColumnType.access(object);

                args.put(index.incrementAndGet(), new Argument(databaseColumnType.getConverters().orElse(null), databaseColumnType.getDataPersister(), value));
            }
            args.put(index.incrementAndGet(), new Argument(idFieldType.getConverters().orElse(null), idFieldType.getDataPersister(), idFieldType.access(object)));

            databaseEngine.update(connection, updateQuery, args);
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * Сохраняет объект в базе
     * Выполняет запрос вида DELETE FROM ... WHERE = object_builder.id
     */
    public <T> void delete(DatabaseConnection connection, T object) throws SQLException {
        try {
            DatabaseEntityPersister entityPersister = metaModel.getPersister(object.getClass());
            IDatabaseColumnType primaryKeyType = entityPersister.getMetadata().getPrimaryKey();
            Object id = primaryKeyType.access(object);
            DeleteQuery deleteQuery = entityPersister.getEntityQuerySpace().getDeleteQuery();

            databaseEngine.delete(connection, deleteQuery, new HashMap<Integer, Argument>() {{
                put(1, new Argument(primaryKeyType.getConverters().orElse(null), primaryKeyType.getDataPersister(), id));
            }});
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * Сохраняет объект в базе
     * Выполняет запрос вида DELETE FROM ... WHERE = object_builder.id
     */
    public <T, ID> void deleteById(DatabaseConnection connection, Class<T> tClass, ID id) throws SQLException {
        DatabaseEntityPersister entityPersister = metaModel.getPersister(tClass);
        DeleteQuery deleteQuery = entityPersister.getEntityQuerySpace().getDeleteQuery();
        IDatabaseColumnType primaryKeyType = entityPersister.getMetadata().getPrimaryKey();

        databaseEngine.delete(connection, deleteQuery, new HashMap<Integer, Argument>() {{
            put(1, new Argument(primaryKeyType.getConverters().orElse(null), primaryKeyType.getDataPersister(), id));
        }});
    }

    /**
     * Возвращает объект по id
     * Выполняет запрос вида SELECT * FROM ... WHERE = id
     */
    public <T, ID> T queryForId(DatabaseConnection connection, Class<T> tClass, ID id) throws SQLException {
        Optional<Object> instance = dao.cacheHelper().get(tClass, id);

        if (instance.isPresent()) {
            return (T) instance.get();
        }

        DatabaseEntityPersister entityPersister = metaModel.getPersister(tClass);
        IDatabaseColumnType primaryKeyType = entityPersister.getMetadata().getPrimaryKey();

        try (DatabaseResults databaseResults =databaseEngine.select(connection, entityPersister.getEntityQuerySpace().getSelectById(), new HashMap<Integer, Argument>() {{
            put(1, new Argument(primaryKeyType.getConverters().orElse(null), primaryKeyType.getDataPersister(), id));
        }})) {
            if (databaseResults.next()) {
                entityPersister.load(dao, databaseResults).iterator().next();
            }
        }

        return null;
    }

    /**
     * Возвращает все объекты из таблицы
     * Выполняет запрос вида SELECT * FROM ...
     */
    public <T> List<T> queryForAll(DatabaseConnection connection, Class<T> tClass) throws SQLException {
        DatabaseEntityPersister entityPersister = metaModel.getPersister(tClass);

        try (DatabaseResults databaseResults = databaseEngine.select(connection, entityPersister.getEntityQuerySpace().getSelectAll(), Collections.emptyMap())) {
            return (List<T>) entityPersister.load(dao, databaseResults);
        }
    }

    public <T> void createIndexes(DatabaseConnection connection, Class<T> tClass) throws SQLException {
        DatabaseEntityPersister entityPersister = metaModel.getPersister(tClass);

        for (CreateIndexQuery createIndexQuery: entityPersister.getEntityQuerySpace().getCreateIndexQuery()) {
            databaseEngine.createIndex(connection, createIndexQuery);
        }
    }

    public <T> void dropIndexes(DatabaseConnection connection, Class<T> tClass) throws SQLException {
        DatabaseEntityPersister entityPersister = metaModel.getPersister(tClass);

        for (DropIndexQuery dropIndexQuery: entityPersister.getEntityQuerySpace().getDropIndexQuery()) {
            databaseEngine.dropIndex(connection, dropIndexQuery);
        }
    }

    public <T> long countOff(DatabaseConnection connection, Class<T> tClass) throws SQLException {
        DatabaseEntityPersister entityPersister = metaModel.getPersister(tClass);
        Select select = entityPersister.getEntityQuerySpace().countOff();

        try (DatabaseResults databaseResults = databaseEngine.select(connection, select, null)) {
            if (databaseResults.next()) {
                return databaseResults.getLong(1);
            }
        }

        return 0;
    }

    public <T> List<T> list(DatabaseConnection connection, CriteriaQuery<?> criteriaQuery) throws SQLException {
        DatabaseEntityPersister entityPersister = metaModel.getPersister(criteriaQuery.getEntityClass());
        Select select = entityPersister.getEntityQuerySpace().getByCriteria(criteriaQuery);

        try (DatabaseResults databaseResults = databaseEngine.select(connection, select, toArguments(entityPersister.getMetadata(), criteriaQuery))) {
            return (List<T>) entityPersister.load(dao, databaseResults);
        }
    }

    public<T> long queryForLong(DatabaseConnection connection, CriteriaQuery<T> criteriaQuery) throws SQLException {
        DatabaseEntityPersister entityPersister = metaModel.getPersister(criteriaQuery.getEntityClass());
        Select select = entityPersister.getEntityQuerySpace().getByCriteriaForLongResult(criteriaQuery);

        try (DatabaseResults databaseResults = databaseEngine.select(connection, select, toArguments(entityPersister.getMetadata(), criteriaQuery))) {
            if (databaseResults.next()) {
                return databaseResults.getLong(1);
            }
        }

        return 0;
    }

    private Map<Integer, Argument> toArguments(DatabaseEntityMetadata<?> metadata, CriteriaQuery<?> criteriaQuery) {
        Map<Integer, Argument> args = new HashMap<>();
        AtomicInteger index = new AtomicInteger();

        for (CriterionArgument criterionArgument : criteriaQuery.getArgs()) {
            IDatabaseColumnType columnType = metadata.getDataTypeByPropertyName(criterionArgument.getProperty());
            List<Converter<?, Object>> converters = columnType.getConverters().isPresent() ? columnType.getConverters().get() : null;
            DataPersister<?> dataPersister = columnType.getDataPersister();

            for (Object arg : criterionArgument.getValues()) {
                args.put(index.incrementAndGet(), new Argument(converters, dataPersister, arg));
            }
        }

        for (Map.Entry<Integer, Object> entry: criteriaQuery.getUserProvidedArgs().entrySet()) {
            DataPersister<?> dataPersister = DataPersisterManager.lookup(entry.getValue().getClass());

            args.put(entry.getKey(), new Argument(null, dataPersister, entry.getValue()));
        }

        return args;
    }
}
