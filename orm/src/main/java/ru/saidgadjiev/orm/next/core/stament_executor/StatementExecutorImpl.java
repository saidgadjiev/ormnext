package ru.saidgadjiev.orm.next.core.stament_executor;

import ru.saidgadjiev.orm.next.core.db.DatabaseType;
import ru.saidgadjiev.orm.next.core.field.field_type.IDBFieldType;
import ru.saidgadjiev.orm.next.core.field.field_type.IndexFieldType;
import ru.saidgadjiev.orm.next.core.query.core.*;
import ru.saidgadjiev.orm.next.core.query.core.clause.select.SelectColumnsList;
import ru.saidgadjiev.orm.next.core.query.core.column_spec.DisplayedOperand;
import ru.saidgadjiev.orm.next.core.query.core.function.CountAll;
import ru.saidgadjiev.orm.next.core.query.visitor.DefaultVisitor;
import ru.saidgadjiev.orm.next.core.query.visitor.QueryElement;
import ru.saidgadjiev.orm.next.core.stament_executor.object.ObjectCreator;
import ru.saidgadjiev.orm.next.core.stament_executor.result_mapper.ResultsMapper;
import ru.saidgadjiev.orm.next.core.table.TableInfo;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс для выполнения sql запросов
 *
 * @param <T>  тип объекта
 * @param <ID> id объекта
 */
@SuppressWarnings("PMD")
public class StatementExecutorImpl<T, ID> implements IStatementExecutor<T, ID> {

    private TableInfo<T> tableInfo;
    
    private ObjectCreator<T> objectCreator;

    private DatabaseType databaseType;
    private ResultsMapper<T> resultsMapper;

    public StatementExecutorImpl(TableInfo<T> tableInfo,
                                 ObjectCreator<T> objectCreator,
                                 DatabaseType databaseType,
                                 ResultsMapper<T> resultsMapper) {
        this.tableInfo = tableInfo;
        this.objectCreator = objectCreator;
        this.databaseType = databaseType;
        this.resultsMapper = resultsMapper;
    }

    /**
     * Сохраняет объект в базе
     * Выполняет запрос вида INSERT INTO ...(colname1, colname2, ...) VALUES(colvalue1, colvalue2, ...)
     */
    @Override
    @SuppressWarnings("unchecked")
    public int create(Connection connection, T object) throws SQLException {
        try {
            CreateQuery query = objectCreator.newObject(object)
                    .createBase(object)
                    .createForeign(object)
                    .query();

            try (PreparedQueryImpl preparedQuery = new PreparedQueryImpl(connection.prepareStatement(getQuery(query), Statement.RETURN_GENERATED_KEYS))) {
                Integer result = preparedQuery.executeUpdate();

                if (tableInfo.getPrimaryKey().isPresent()) {
                    IDBFieldType idField = tableInfo.getPrimaryKey().get();

                    ResultSet resultSet = preparedQuery.getGeneratedKeys();
                    try (GeneratedKeys generatedKeys = new GeneratedKeys(resultSet, resultSet.getMetaData())) {
                        if (generatedKeys.next()) {
                            try {
                                idField.assignId(object, generatedKeys.getGeneratedKey());
                            } catch (IllegalAccessException ex) {
                                throw new SQLException(ex);
                            }
                        }
                    }
                }

                return result;
            }
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    @Override
    public boolean createTable(Connection connection, boolean ifNotExists) throws SQLException {
        
        CreateTableQuery createTableQuery = CreateTableQuery.buildQuery(
                tableInfo,
                ifNotExists
        );

        try (PreparedQueryImpl preparedQuery = new PreparedQueryImpl(connection.prepareStatement(getQuery(createTableQuery)))) {
            preparedQuery.executeUpdate();

            return true;
        }
    }

    @Override
    public boolean dropTable(Connection connection, boolean ifExists) throws SQLException {
        
        DropTableQuery dropTableQuery = DropTableQuery.buildQuery(tableInfo.getTableName(), ifExists);

        try (PreparedQueryImpl preparedQuery = new PreparedQueryImpl(connection.prepareStatement(getQuery(dropTableQuery)))) {
            preparedQuery.executeUpdate();

            return true;
        }
    }

    /**
     * Обновляет объект в базе
     * Выполняет запрос вида UPDATE ... SET colname1 = colvalue1 SET colname2 = colvalue2 WHERE = object_builder.id
     */
    @Override
    public int update(Connection connection, T object) throws SQLException {
        
        IDBFieldType idFieldType = tableInfo.getPrimaryKey().get();
        UpdateQuery query = UpdateQuery.buildQuery(
                tableInfo.getTableName(),
                tableInfo.toDBFieldTypes(),
                idFieldType.getColumnName(),
                object
        );

        try (PreparedQueryImpl preparedQuery = new PreparedQueryImpl(connection.prepareStatement(getQuery(query)))) {
            preparedQuery.setObject(1, idFieldType.access(object));

            return preparedQuery.executeUpdate();
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * Сохраняет объект в базе
     * Выполняет запрос вида DELETE FROM ... WHERE = object_builder.id
     */
    @Override
    public int delete(Connection connection, T object) throws SQLException {
        try {
            IDBFieldType dbFieldType = tableInfo.getPrimaryKey().get();
            Object id = dbFieldType.access(object);
            DeleteQuery query = DeleteQuery.buildQuery(tableInfo.getTableName(), dbFieldType.getColumnName());

            try (PreparedQueryImpl preparedQuery = new PreparedQueryImpl(connection.prepareStatement(getQuery(query)))) {
                preparedQuery.setObject(1, id);

                return preparedQuery.executeUpdate();
            }
        } catch (IllegalAccessException | InvocationTargetException ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * Сохраняет объект в базе
     * Выполняет запрос вида DELETE FROM ... WHERE = object_builder.id
     */
    @Override
    public int deleteById(Connection connection, ID id) throws SQLException {
        IDBFieldType dbFieldType = tableInfo.getPrimaryKey().get();
        DeleteQuery query = DeleteQuery.buildQuery(tableInfo.getTableName(), dbFieldType.getColumnName());

        try (PreparedQueryImpl preparedQuery = new PreparedQueryImpl(connection.prepareStatement(getQuery(query)))) {
            preparedQuery.setObject(1, id);

            return preparedQuery.executeUpdate();
        }
    }

    /**
     * Возвращает объект по id
     * Выполняет запрос вида SELECT * FROM ... WHERE = id
     */
    @Override
    public T queryForId(Connection connection, ID id) throws SQLException {
        IDBFieldType dbFieldType = tableInfo.getPrimaryKey().get();
        Select query = Select.buildQueryById(tableInfo.getTableName(), dbFieldType, id);

        try (PreparedQueryImpl preparedQuery = new PreparedQueryImpl(connection.prepareStatement(getQuery(query)))) {
            try (DatabaseResults databaseResults = preparedQuery.executeQuery()) {
                if (databaseResults.next()) {
                    return resultsMapper.mapResults(databaseResults);
                }
            } catch (Exception ex) {
                throw new SQLException(ex);
            }
        }

        return null;
    }

    /**
     * Возвращает все объекты из таблицы
     * Выполняет запрос вида SELECT * FROM ...
     */
    @Override
    public List<T> queryForAll(Connection connection) throws SQLException {
        Select select = Select.buildQueryForAll(tableInfo.getTableName());
        List<T> resultObjectList = new ArrayList<>();

        try (PreparedQueryImpl preparedQueryImpl = new PreparedQueryImpl(connection.prepareStatement(getQuery(select)))) {
            try (DatabaseResults databaseResults = preparedQueryImpl.executeQuery()) {
                while (databaseResults.next()) {
                    resultObjectList.add(resultsMapper.mapResults(databaseResults));
                }
            } catch (Exception ex) {
                throw new SQLException(ex);
            }

        }

        return resultObjectList;
    }


    @Override
    public void createIndexes(Connection connection) throws SQLException {
        List<IndexFieldType> indexFieldTypes = tableInfo.getIndexFieldTypes();

        for (IndexFieldType indexFieldType : indexFieldTypes) {
            CreateIndexQuery createIndexQuery = new CreateIndexQuery(indexFieldType);

            try (PreparedQueryImpl preparedQuery = new PreparedQueryImpl(connection.prepareStatement(getQuery(createIndexQuery)))) {
                preparedQuery.executeUpdate();
            }
        }
    }

    @Override
    public void dropIndexes(Connection connection) throws SQLException {
        List<IndexFieldType> indexFieldTypes = tableInfo.getIndexFieldTypes();

        for (IndexFieldType indexFieldType : indexFieldTypes) {
            DropIndexQuery dropIndexQuery = DropIndexQuery.build(indexFieldType.getName());

            try (PreparedQueryImpl preparedQuery = new PreparedQueryImpl(connection.prepareStatement(getQuery(dropIndexQuery)))) {
                preparedQuery.executeUpdate();
            }
        }
    }

    @Override
    public <R> GenericResults<R> query(Connection connection, String query, ResultsMapper<R> resultsMapper) throws SQLException {
        try (PreparedQueryImpl preparedQueryImpl = new PreparedQueryImpl(connection.prepareStatement(query))) {
            try (DatabaseResults databaseResults = preparedQueryImpl.executeQuery()) {
                return new GenericResults<R>() {
                    @Override
                    public List<R> getResults() throws SQLException {
                        try {
                            List<R> objects = new ArrayList<>();

                            while (databaseResults.next()) {
                                objects.add(resultsMapper.mapResults(databaseResults));
                            }

                            return objects;
                        } catch (Exception e) {
                            throw new SQLException(e);
                        }
                    }

                    @Override
                    public R getFirstResult() throws SQLException {
                        return null;
                    }
                };
            } catch (Exception ex) {
                throw new SQLException(ex);
            }
        }
    }

    @Override
    public long query(String query, Connection connection) throws SQLException {
        try (PreparedQueryImpl preparedQueryImpl = new PreparedQueryImpl(connection.prepareStatement(query))) {
            try (DatabaseResults databaseResults = preparedQueryImpl.executeQuery()) {
                if (databaseResults.next()) {
                    return databaseResults.getLong(1);
                }
            } catch (Exception ex) {
                throw new SQLException(ex);
            }
        }

        throw new SQLException("No result found in queryForLong: " + query);
    }

    @Override
    public long countOff(Connection connection) throws SQLException {
        Select select = Select.buildQueryForAll(tableInfo.getTableName());
        SelectColumnsList selectColumnsList = new SelectColumnsList();

        selectColumnsList.addColumn(new DisplayedOperand(new CountAll()));
        select.setSelectColumnsStrategy(selectColumnsList);

        return query(getQuery(select), connection);
    }

    private String getQuery(QueryElement queryElement) {
        DefaultVisitor defaultVisitor = new DefaultVisitor(databaseType);

        queryElement.accept(defaultVisitor);

        return defaultVisitor.getQuery();
    }
}
