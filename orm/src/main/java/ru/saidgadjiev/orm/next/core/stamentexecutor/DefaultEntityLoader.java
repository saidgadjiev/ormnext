package ru.saidgadjiev.orm.next.core.stamentexecutor;

import ru.saidgadjiev.orm.next.core.dao.CriteriaQuery;
import ru.saidgadjiev.orm.next.core.dao.Session;
import ru.saidgadjiev.orm.next.core.db.DatabaseType;
import ru.saidgadjiev.orm.next.core.field.fieldtype.IDatabaseColumnType;
import ru.saidgadjiev.orm.next.core.query.core.*;
import ru.saidgadjiev.orm.next.core.query.visitor.DefaultVisitor;
import ru.saidgadjiev.orm.next.core.query.visitor.QueryElement;
import ru.saidgadjiev.orm.next.core.stamentexecutor.object.operation.ForeignCreator;
import ru.saidgadjiev.orm.next.core.stamentexecutor.rowreader.RowReader;
import ru.saidgadjiev.orm.next.core.stamentexecutor.rowreader.RowResult;
import ru.saidgadjiev.orm.next.core.table.internal.metamodel.MetaModel;
import ru.saidgadjiev.orm.next.core.table.internal.persister.DatabaseEntityPersister;
import ru.saidgadjiev.orm.next.core.utils.ArgumentUtils;

import java.sql.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Класс для выполнения sql запросов
 */
@SuppressWarnings("PMD")
public class DefaultEntityLoader {

    private Session session;

    private MetaModel metaModel;

    private DatabaseType databaseType;

    private ForeignCreator foreignCreator;

    public DefaultEntityLoader(Session session,
                               MetaModel metaModel,
                               DatabaseType databaseType) {
        this.session = session;
        this.metaModel = metaModel;
        this.databaseType = databaseType;
        this.foreignCreator = new ForeignCreator(session, metaModel);
    }

    public <T> int create(Connection connection, Collection<T> objects) throws SQLException {
        try {
            if (objects.isEmpty()) {
                return 0;
            }
            Class<T> tClass = (Class<T>) objects.iterator().next().getClass();
            DatabaseEntityPersister entityPersister = metaModel.getPersister(tClass);
            CreateQuery createQuery = entityPersister.getEntityQuerySpace().getCreateQuery(objects.size());
            String query = getQuery(createQuery);
            Map<Integer, Object> args = new HashMap<>();
            AtomicInteger atomicInteger = new AtomicInteger();

            for (T object : objects) {
                foreignCreator.execute(object);

                for (Map.Entry<Integer, Object> entry : ArgumentUtils.eject(object, entityPersister.getMetadata()).entrySet()) {
                    args.put(atomicInteger.incrementAndGet(), entry.getValue());
                }
            }
            try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                for (Map.Entry<Integer, Object> entry: args.entrySet()) {
                    statement.setObject(entry.getKey(), entry.getValue());
                }
                return statement.executeUpdate();
            }
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * Сохраняет объект в базе
     * Выполняет запрос вида INSERT INTO ...(colname1, colname2, ...) VALUES(colvalue1, colvalue2, ...)
     */
    @SuppressWarnings("unchecked")
    public <T> int create(Connection connection, T object) throws SQLException {
        try {
            DatabaseEntityPersister entityPersister = metaModel.getPersister(object.getClass());
            CreateQuery createQuery = entityPersister.getEntityQuerySpace().getCreateQuery();

            foreignCreator.execute(object);
            String query = getQuery(createQuery);

            try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                for (Map.Entry<Integer, Object> entry : ArgumentUtils.eject(object, entityPersister.getMetadata()).entrySet()) {
                    statement.setObject(entry.getKey(), entry.getValue());
                }
                Integer result = statement.executeUpdate();

                IDatabaseColumnType idField = entityPersister.getMetadata().getPrimaryKey();

                ResultSet resultSet = statement.getGeneratedKeys();
                try (GeneratedKeys generatedKeys = new GeneratedKeys(resultSet, resultSet.getMetaData())) {
                    if (generatedKeys.next()) {
                        try {
                            idField.assignId(object, generatedKeys.getGeneratedKey());
                        } catch (IllegalAccessException ex) {
                            throw new SQLException(ex);
                        }
                    }
                }

                return result;
            }
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    public <T> boolean createTable(Connection connection, Class<T> tClass, boolean ifNotExists) throws SQLException {
        CreateTableQuery createTableQuery = metaModel.getPersister(tClass).getEntityQuerySpace().createTableQuery(ifNotExists);

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(getQuery(createTableQuery));

            return true;
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    public <T> boolean dropTable(Connection connection, Class<T> tClass, boolean ifExists) throws SQLException {
        DropTableQuery dropTableQuery = metaModel.getPersister(tClass).getEntityQuerySpace().getDropTableQuery(ifExists);

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(getQuery(dropTableQuery));

            return true;
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * Обновляет объект в базе
     * Выполняет запрос вида UPDATE ... SET colname1 = colvalue1 SET colname2 = colvalue2 WHERE = object_builder.id
     */
    public <T> int update(Connection connection, T object) throws SQLException {
        DatabaseEntityPersister entityPersister = metaModel.getPersister(object.getClass());
        IDatabaseColumnType idFieldType = entityPersister.getMetadata().getPrimaryKey();
        UpdateQuery updateQuery = entityPersister.getEntityQuerySpace().getUpdateQuery();
        String query = getQuery(updateQuery);
        AtomicInteger index = new AtomicInteger();

        try (PreparedStatement preparedQuery = connection.prepareStatement(query)) {
            for (IDatabaseColumnType IDatabaseColumnType : entityPersister.getMetadata().toDBFieldTypes()) {
                Object value = IDatabaseColumnType.access(object);

                preparedQuery.setObject(index.incrementAndGet(), value);
            }
            preparedQuery.setObject(index.incrementAndGet(), idFieldType.access(object));

            return preparedQuery.executeUpdate();
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * Сохраняет объект в базе
     * Выполняет запрос вида DELETE FROM ... WHERE = object_builder.id
     */
    public <T> int delete(Connection connection, T object) throws SQLException {
        try {
            DatabaseEntityPersister entityPersister = metaModel.getPersister(object.getClass());
            IDatabaseColumnType dbFieldType = entityPersister.getMetadata().getPrimaryKey();
            Object id = dbFieldType.access(object);
            DeleteQuery deleteQuery = entityPersister.getEntityQuerySpace().getDeleteQuery();
            String query = getQuery(deleteQuery);

            try (PreparedStatement preparedQuery = connection.prepareStatement(query)) {
                preparedQuery.setObject(1, id);

                return preparedQuery.executeUpdate();
            }
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * Сохраняет объект в базе
     * Выполняет запрос вида DELETE FROM ... WHERE = object_builder.id
     */
    public <T, ID> int deleteById(Connection connection, Class<T> tClass, ID id) throws SQLException {
        DatabaseEntityPersister entityPersister = metaModel.getPersister(tClass);
        DeleteQuery deleteQuery = entityPersister.getEntityQuerySpace().getDeleteQuery();
        String query = getQuery(deleteQuery);

        try (PreparedStatement preparedQuery = connection.prepareStatement(query)) {
            preparedQuery.setObject(1, id);

            return preparedQuery.executeUpdate();
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * Возвращает объект по id
     * Выполняет запрос вида SELECT * FROM ... WHERE = id
     */
    public <T, ID> T queryForId(Connection connection, Class<T> tClass, ID id) throws SQLException {
        Optional<Object> instance = session.cacheHelper().get(tClass, id);

        if (instance.isPresent()) {
            return (T) instance.get();
        }

        DatabaseEntityPersister entityPersister = metaModel.getPersister(tClass);
        String query = getQuery(entityPersister.getEntityQuerySpace().getSelectById());

        try (PreparedStatement preparedQuery = connection.prepareStatement(query)) {
            try (ResultSet databaseResults = preparedQuery.executeQuery()) {
                List<Object> results = load(entityPersister.getRowReader(), session, databaseResults);

                if (results.isEmpty()) {
                    return null;
                }

                return (T) results.iterator().next();
            }
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * Возвращает все объекты из таблицы
     * Выполняет запрос вида SELECT * FROM ...
     */
    public <T> List<T> queryForAll(Connection connection, Class<T> tClass) throws SQLException {
        DatabaseEntityPersister entityPersister = metaModel.getPersister(tClass);
        String query = getQuery(entityPersister.getEntityQuerySpace().getSelectAll());

        try (Statement statement = connection.createStatement()) {
            try (ResultSet databaseResults = statement.executeQuery(query)) {
                return (List<T>) load(entityPersister.getRowReader(), session, databaseResults);
            }
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    public <T> void createIndexes(Connection connection, Class<T> tClass) throws SQLException {
        DatabaseEntityPersister entityPersister = metaModel.getPersister(tClass);
        Iterator<CreateIndexQuery> indexQueryIterator = entityPersister.getEntityQuerySpace().getCreateIndexQuery();

        while (indexQueryIterator.hasNext()) {
            CreateIndexQuery createIndexQuery = indexQueryIterator.next();

            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(getQuery(createIndexQuery));
            } catch (Exception ex) {
                throw new SQLException(ex);
            }
        }
    }

    public <T> void dropIndexes(Connection connection, Class<T> tClass) throws SQLException {
        DatabaseEntityPersister entityPersister = metaModel.getPersister(tClass);
        Iterator<DropIndexQuery> indexQueryIterator = entityPersister.getEntityQuerySpace().getDropIndexQuery();

        while (indexQueryIterator.hasNext()) {
            DropIndexQuery createIndexQuery = indexQueryIterator.next();

            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(getQuery(createIndexQuery));
            } catch (Exception ex) {
                throw new SQLException(ex);
            }
        }
    }

    private long queryForLong(Connection connection, String query) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            try (ResultSet databaseResults = statement.executeQuery(query)) {
                if (databaseResults.next()) {
                    return databaseResults.getLong(1);
                }
            }
        } catch (Exception ex) {
            throw new SQLException(ex);
        }

        throw new SQLException("No result found in queryForLong: " + query);
    }

    public <T> long countOff(Connection connection, Class<T> tClass) throws SQLException {
        DatabaseEntityPersister entityPersister = metaModel.getPersister(tClass);
        Select select = entityPersister.getEntityQuerySpace().countOff();

        return queryForLong(connection, getQuery(select));
    }

    public <T> List<T> list(Connection connection, CriteriaQuery criteriaQuery) throws SQLException {
        DatabaseEntityPersister entityPersister = metaModel.getPersister(criteriaQuery.getPersistentClass());
        Select select = entityPersister.getEntityQuerySpace().getByCriteria(criteriaQuery);
        String query = getQuery(select);
        AtomicInteger integer = new AtomicInteger();

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            for (Object arg: criteriaQuery.getArgs()) {
                statement.setObject(integer.incrementAndGet(), arg);
            }
            try (ResultSet databaseResults = statement.executeQuery()) {
                return (List<T>) load(entityPersister.getRowReader(), session, databaseResults);
            }
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    private String getQuery(QueryElement queryElement) {
        DefaultVisitor defaultVisitor = new DefaultVisitor(databaseType);

        queryElement.accept(defaultVisitor);

        return defaultVisitor.getQuery();
    }

    private List<Object> load(RowReader rowReader, Session session, ResultSet databaseResults) throws SQLException {
        ResultSetContext resultSetContext = new ResultSetContext(session, databaseResults);
        List<Object> results = new ArrayList<>();

        while(databaseResults.next()) {
            RowResult<Object> rowResult = rowReader.startRead(resultSetContext);

            if (rowResult.isNew()) {
                results.add(rowResult.getResult());
            }
            rowReader.finishRead(resultSetContext);
        }

        return results;
    }
}
