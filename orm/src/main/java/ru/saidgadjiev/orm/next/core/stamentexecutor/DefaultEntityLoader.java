package ru.saidgadjiev.orm.next.core.stamentexecutor;

import ru.saidgadjiev.orm.next.core.criteria.impl.CriteriaQuery;
import ru.saidgadjiev.orm.next.core.criteria.impl.SimpleCriteriaQuery;
import ru.saidgadjiev.orm.next.core.dao.Dao;
import ru.saidgadjiev.orm.next.core.dao.DatabaseEngine;
import ru.saidgadjiev.orm.next.core.field.fieldtype.IDatabaseColumnType;
import ru.saidgadjiev.orm.next.core.query.core.*;
import ru.saidgadjiev.orm.next.core.stamentexecutor.object.operation.ForeignCreator;
import ru.saidgadjiev.orm.next.core.stamentexecutor.rowreader.RowReader;
import ru.saidgadjiev.orm.next.core.stamentexecutor.rowreader.RowResult;
import ru.saidgadjiev.orm.next.core.support.DatabaseConnection;
import ru.saidgadjiev.orm.next.core.support.OrmNextResultSet;
import ru.saidgadjiev.orm.next.core.table.internal.metamodel.MetaModel;
import ru.saidgadjiev.orm.next.core.table.internal.persister.DatabaseEntityPersister;
import ru.saidgadjiev.orm.next.core.utils.ArgumentUtils;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.*;

/**
 * Класс для выполнения sql запросов
 */
@SuppressWarnings("PMD")
public class DefaultEntityLoader {

    private Dao dao;

    private MetaModel metaModel;

    private DatabaseEngine databaseEngine;

    private ForeignCreator foreignCreator;

    public DefaultEntityLoader(Dao dao) {
        this.dao = dao;
        this.metaModel = dao.getMetaModel();
        this.databaseEngine = dao.getDatabaseEngine();
        this.foreignCreator = new ForeignCreator(dao, metaModel);
    }

    public <T> int create(DatabaseConnection connection, Collection<T> objects) throws SQLException {
        try {
            if (objects.isEmpty()) {
                return 0;
            }
            Class<T> tClass = (Class<T>) objects.iterator().next().getClass();
            DatabaseEntityPersister entityPersister = metaModel.getPersister(tClass);
            CreateQuery createQuery = entityPersister.getEntityQuerySpace().getCreateQuery(objects.size());
            List<Object> args = new ArrayList<>();

            for (T object : objects) {
                foreignCreator.execute(object);

                args.addAll(ArgumentUtils.eject(object, entityPersister.getMetadata()));
            }

            return databaseEngine.create(connection, createQuery, args, resultSetObject -> {});
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * Сохраняет объект в базе
     * Выполняет запрос вида INSERT INTO ...(colname1, colname2, ...) VALUES(colvalue1, colvalue2, ...)
     */
    @SuppressWarnings("unchecked")
    public <T> int create(DatabaseConnection connection, T object) throws SQLException {
        try {
            DatabaseEntityPersister entityPersister = metaModel.getPersister(object.getClass());
            CreateQuery createQuery = entityPersister.getEntityQuerySpace().getCreateQuery();
            IDatabaseColumnType idField = entityPersister.getMetadata().getPrimaryKey();

            foreignCreator.execute(object);
            return databaseEngine.create(connection, createQuery, ArgumentUtils.eject(object, entityPersister.getMetadata()), new DatabaseEngine.ResultSetProcessor() {
                @Override
                public void process(OrmNextResultSet resultSetObject) throws SQLException {
                    OrmNextResultSet.GeneratedKeys generatedKeys = resultSetObject.generatedKeys();

                    if (generatedKeys.next()) {
                        try {
                            idField.assignId(object, new GeneratedKeysEjector(generatedKeys.getGeneratedKey(), generatedKeys.getType()).getGeneratedKey());
                        } catch (IllegalAccessException | InvocationTargetException ex) {
                            throw new SQLException(ex);
                        }
                    }
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
    public <T> int update(DatabaseConnection connection, T object) throws SQLException {
        DatabaseEntityPersister entityPersister = metaModel.getPersister(object.getClass());
        IDatabaseColumnType idFieldType = entityPersister.getMetadata().getPrimaryKey();
        UpdateQuery updateQuery = entityPersister.getEntityQuerySpace().getUpdateQuery();

        try {
            List<Object> args = new ArrayList<>();

            for (IDatabaseColumnType IDatabaseColumnType : entityPersister.getMetadata().toDBFieldTypes()) {
                Object value = IDatabaseColumnType.access(object);

                args.add(value);
            }
            args.add(idFieldType.access(object));

            return databaseEngine.update(connection, updateQuery, args);
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * Сохраняет объект в базе
     * Выполняет запрос вида DELETE FROM ... WHERE = object_builder.id
     */
    public <T> int delete(DatabaseConnection connection, T object) throws SQLException {
        try {
            DatabaseEntityPersister entityPersister = metaModel.getPersister(object.getClass());
            IDatabaseColumnType dbFieldType = entityPersister.getMetadata().getPrimaryKey();
            Object id = dbFieldType.access(object);
            DeleteQuery deleteQuery = entityPersister.getEntityQuerySpace().getDeleteQuery();

            return databaseEngine.delete(connection, deleteQuery, Arrays.asList(id));
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * Сохраняет объект в базе
     * Выполняет запрос вида DELETE FROM ... WHERE = object_builder.id
     */
    public <T, ID> int deleteById(DatabaseConnection connection, Class<T> tClass, ID id) throws SQLException {
        DatabaseEntityPersister entityPersister = metaModel.getPersister(tClass);
        DeleteQuery deleteQuery = entityPersister.getEntityQuerySpace().getDeleteQuery();

        return databaseEngine.delete(connection, deleteQuery, Arrays.asList(id));
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

        databaseEngine.select(connection, entityPersister.getEntityQuerySpace().getSelectById(), Arrays.asList(id), resultSetObject -> {
            results.addAll(load(entityPersister.getRowReader(), dao, resultSetObject));
        });

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

        databaseEngine.select(connection, entityPersister.getEntityQuerySpace().getSelectAll(), null, resultSetObject -> {
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
                result[0] = (Long) resultSetObject.getObject(1);
            }
        });

        return result[0];
    }

    public <T> List<T> list(DatabaseConnection connection, CriteriaQuery criteriaQuery) throws SQLException {
        DatabaseEntityPersister entityPersister = metaModel.getPersister(criteriaQuery.getPersistentClass());
        Select select = entityPersister.getEntityQuerySpace().getByCriteria(criteriaQuery);
        List<Object> results = new ArrayList<>();

        databaseEngine.select(connection, select, criteriaQuery.getArgs(), resultSetObject -> {
            results.addAll(load(entityPersister.getRowReader(), dao, resultSetObject));
        });

        return (List<T>) results;
    }

    public long queryForLong(DatabaseConnection connection, SimpleCriteriaQuery simpleCriteriaQuery) throws SQLException {
        DatabaseEntityPersister entityPersister = metaModel.getPersister(simpleCriteriaQuery.getPersistentClass());
        Select select = entityPersister.getEntityQuerySpace().getByCriteria(simpleCriteriaQuery);
        Long [] result = new Long[]{ null };

        databaseEngine.select(connection, select, simpleCriteriaQuery.getArgs(), resultSetObject -> {
            if (resultSetObject.next()) {
                result[0] = (Long) resultSetObject.getObject(1);
            }
        });

        return result[0];
    }

    private List<Object> load(RowReader rowReader, Dao dao, OrmNextResultSet databaseResults) throws SQLException {
        ResultSetContext resultSetContext = new ResultSetContext(dao, databaseResults);
        List<Object> results = new ArrayList<>();

        while (databaseResults.next()) {
            RowResult<Object> rowResult = rowReader.startRead(resultSetContext);

            if (rowResult.isNew()) {
                results.add(rowResult.getResult());
            }
            rowReader.finishRead(resultSetContext);
        }

        return results;
    }

}
