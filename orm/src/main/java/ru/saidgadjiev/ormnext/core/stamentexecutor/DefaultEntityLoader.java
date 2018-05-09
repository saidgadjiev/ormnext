package ru.saidgadjiev.ormnext.core.stamentexecutor;

import ru.saidgadjiev.ormnext.core.criteria.impl.CriteriaQuery;
import ru.saidgadjiev.ormnext.core.criteria.impl.CriterionArgument;
import ru.saidgadjiev.ormnext.core.criteria.impl.SimpleCriteriaQuery;
import ru.saidgadjiev.ormnext.core.dao.DatabaseEngine;
import ru.saidgadjiev.ormnext.core.dao.InternalSession;
import ru.saidgadjiev.ormnext.core.field.DataPersisterManager;
import ru.saidgadjiev.ormnext.core.field.fieldtype.ForeignColumnType;
import ru.saidgadjiev.ormnext.core.field.fieldtype.IDatabaseColumnType;
import ru.saidgadjiev.ormnext.core.field.persister.Converter;
import ru.saidgadjiev.ormnext.core.field.persister.DataPersister;
import ru.saidgadjiev.ormnext.core.query.core.*;
import ru.saidgadjiev.ormnext.core.stamentexecutor.rowreader.RowReader;
import ru.saidgadjiev.ormnext.core.stamentexecutor.rowreader.RowResult;
import ru.saidgadjiev.ormnext.core.support.DatabaseConnection;
import ru.saidgadjiev.ormnext.core.support.DatabaseResultSet;
import ru.saidgadjiev.ormnext.core.table.internal.metamodel.DatabaseEntityMetadata;
import ru.saidgadjiev.ormnext.core.table.internal.metamodel.MetaModel;
import ru.saidgadjiev.ormnext.core.table.internal.persister.DatabaseEntityPersister;
import ru.saidgadjiev.ormnext.core.utils.ArgumentUtils;

import java.sql.SQLException;
import java.util.*;

/**
 * Класс для выполнения sql запросов
 */
@SuppressWarnings("PMD")
public class DefaultEntityLoader {

    private InternalSession dao;

    private MetaModel metaModel;

    private DatabaseEngine databaseEngine;

    public DefaultEntityLoader(InternalSession dao, MetaModel metaModel, DatabaseEngine databaseEngine) {
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
            databaseEngine.create(connection, createQuery, argumentMap.values(), resultSetObject -> {
                if (resultSetObject.next()) {
                    DataPersister<?> dataPersister = idField.getDataPersister();

                    idField.assignId(object, (Number) dataPersister.readValue(resultSetObject, 1));
                }
            });
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
        IDatabaseColumnType idFieldType = entityPersister.getMetadata().getPrimaryKey();
        UpdateQuery updateQuery = entityPersister.getEntityQuerySpace().getUpdateQuery();

        try {
            List<Argument> args = new ArrayList<>();

            for (IDatabaseColumnType databaseColumnType : entityPersister.getMetadata().toDBFieldTypes()) {
                Object value = databaseColumnType.access(object);

                args.add(new Argument(databaseColumnType.getConverters().orElse(null), databaseColumnType.getDataPersister(), value));
            }
            args.add(new Argument(idFieldType.getConverters().orElse(null), idFieldType.getDataPersister(), idFieldType.access(object)));

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

            databaseEngine.delete(connection, deleteQuery, new ArrayList<Argument>() {{
                add(new Argument(primaryKeyType.getConverters().orElse(null), primaryKeyType.getDataPersister(), id));
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

        databaseEngine.delete(connection, deleteQuery, new ArrayList<Argument>() {{
            add(new Argument(primaryKeyType.getConverters().orElse(null), primaryKeyType.getDataPersister(), id));
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
        List<Object> results = new ArrayList<>();
        IDatabaseColumnType primaryKeyType = entityPersister.getMetadata().getPrimaryKey();

        databaseEngine.select(connection, entityPersister.getEntityQuerySpace().getSelectById(), new ArrayList<Argument>() {{
            add(new Argument(primaryKeyType.getConverters().orElse(null), primaryKeyType.getDataPersister(), id));
        }}, resultSetObject -> results.addAll(load(entityPersister.getRowReader(), dao, resultSetObject)));

        if (results.isEmpty()) {
            return null;
        }

        return (T) results.iterator().next();
    }

    /**
     * Возвращает все объекты из таблицы
     * Выполняет запрос вида SELECT * FROM ...
     */
    public <T> List<T> queryForAll(DatabaseConnection connection, Class<T> tClass) throws SQLException {
        DatabaseEntityPersister entityPersister = metaModel.getPersister(tClass);
        List<Object> results = new ArrayList<>();

        databaseEngine.select(connection, entityPersister.getEntityQuerySpace().getSelectAll(), Collections.emptyList(), resultSetObject -> {
            results.addAll(load(entityPersister.getRowReader(), dao, resultSetObject));
        });

        return (List<T>) results;
    }

    public <T> void createIndexes(DatabaseConnection connection, Class<T> tClass) throws SQLException {
        DatabaseEntityPersister entityPersister = metaModel.getPersister(tClass);
        Iterator<CreateIndexQuery> indexQueryIterator = entityPersister.getEntityQuerySpace().getCreateIndexQuery();

        databaseEngine.createIndexes(connection, indexQueryIterator);
    }

    public <T> void dropIndexes(DatabaseConnection connection, Class<T> tClass) throws SQLException {
        DatabaseEntityPersister entityPersister = metaModel.getPersister(tClass);
        Iterator<DropIndexQuery> indexQueryIterator = entityPersister.getEntityQuerySpace().getDropIndexQuery();

        databaseEngine.dropIndexes(connection, indexQueryIterator);
    }

    public <T> long countOff(DatabaseConnection connection, Class<T> tClass) throws SQLException {
        DatabaseEntityPersister entityPersister = metaModel.getPersister(tClass);
        Select select = entityPersister.getEntityQuerySpace().countOff();
        Long [] result = new Long[]{ null };

        databaseEngine.select(connection, select, null, resultSetObject -> {
            if (resultSetObject.next()) {
                result[0] = resultSetObject.getLong(1);
            }
        });

        return result[0];
    }

    public <T> List<T> list(DatabaseConnection connection, CriteriaQuery<?> criteriaQuery) throws SQLException {
        DatabaseEntityPersister entityPersister = metaModel.getPersister(criteriaQuery.getPersistentClass());
        DatabaseEntityMetadata<?> metadata = entityPersister.getMetadata();
        Select select = entityPersister.getEntityQuerySpace().getByCriteria(criteriaQuery);
        List<Object> results = new ArrayList<>();

        databaseEngine.select(connection, select, toArguments(entityPersister.getMetadata(), criteriaQuery.getArgs()), resultSetObject -> {
            results.addAll(load(entityPersister.getRowReader(), dao, resultSetObject));
        });

        return (List<T>) results;
    }

    public long queryForLong(DatabaseConnection connection, SimpleCriteriaQuery simpleCriteriaQuery) throws SQLException {
        DatabaseEntityPersister entityPersister = metaModel.getPersister(simpleCriteriaQuery.getPersistentClass());
        Select select = entityPersister.getEntityQuerySpace().getByCriteria(simpleCriteriaQuery);
        Long [] result = new Long[]{ null };

        databaseEngine.select(connection, select, toArguments(entityPersister.getMetadata(), simpleCriteriaQuery.getArgs()), resultSetObject -> {
            if (resultSetObject.next()) {
                result[0] = resultSetObject.getLong(1);
            }
        });

        return result[0];
    }

    private List<Object> load(RowReader rowReader, InternalSession dao, DatabaseResultSet databaseResults) throws SQLException {
        ResultSetContext resultSetContext = new ResultSetContext(dao, databaseResults);
        List<Object> results = new ArrayList<>();

        while (databaseResults.next()) {
            RowResult<Object> rowResult = rowReader.startRead(resultSetContext);

            if (rowResult.isNew()) {
                results.add(rowResult.getResult());
            }
        }
        rowReader.finishRead(resultSetContext);

        return results;
    }

    private List<Argument> toArguments(DatabaseEntityMetadata<?> metadata, List<CriterionArgument> arguments) {
        List<Argument> args = new ArrayList<>();

        for (CriterionArgument criterionArgument: arguments) {
            if (criterionArgument.getPropertyName() != null) {
                IDatabaseColumnType columnType = metadata.getDataTypeByPropertyName(criterionArgument.getPropertyName());
                List<Converter<?, Object>> converters = columnType.getConverters().isPresent() ? columnType.getConverters().get() : null;
                DataPersister<?> dataPersister = columnType.getDataPersister();

                for (Object arg: criterionArgument.getValues()) {
                    args.add(new Argument(converters, dataPersister, arg));
                }
            } else {
                DataPersister<?> dataPersister = DataPersisterManager.lookup(criterionArgument.getValues().getClass());

                for (Object arg: criterionArgument.getValues()) {
                    args.add(new Argument(null, dataPersister, arg));
                }
            }
        }

        return args;
    }
}
