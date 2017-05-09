package dao;

import clause.QueryBuilder;
import clause.Update;
import clause.Where;
import field.DBField;
import field.DataType;
import field.OneToOne;
import support.JDBCConnectionSource;
import table.DBTable;
import table.TableInfo;
import utils.ReflectionUtils;
import utils.StatementExecutor;
import utils.TableUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by said on 25.02.17.
 */
public class DaoImpl<T> implements Dao<T> {

    private JDBCConnectionSource connectionSource;
    private final TableInfo<T> tableInfo;
    private StatementExecutor statementExecutor;

    public DaoImpl(JDBCConnectionSource connectionSource, Class<T> dbTable) {
        this.connectionSource = connectionSource;
        this.tableInfo = new TableInfo<>(dbTable);
        this.statementExecutor = new StatementExecutor(connectionSource);
    }

    public void create(T object) throws SQLException {
        statementExecutor.create(object);
    }

    @Override
    public T queryForId(int id) throws SQLException {
        T result = (T) statementExecutor.queryForId(tableInfo, id);
        for (Field field: tableInfo.getManyToManyRelations()) {
            statementExecutor.fillManyToMany(tableInfo, new TableInfo<>(ReflectionUtils.getCollectionGenericClass(field)), field, result);
        }

        return result;
    }

    @Override
    public List<T> queryForAll() throws SQLException {
        List<T> result = (List<T>) statementExecutor.queryForAll(tableInfo);

        for (T object: result) {
            for (Field field : tableInfo.getManyToManyRelations()) {
                statementExecutor.fillManyToMany(tableInfo, new TableInfo<>(ReflectionUtils.getCollectionGenericClass(field)), field, object);
            }
        }

        return result;
    }

    @Override
    public List<T> queryForAll(String sql) throws SQLException {
        List<T> result = (List<T>) statementExecutor.queryForAll(tableInfo, sql);

        for (T object: result) {
            for (Field field : tableInfo.getManyToManyRelations()) {
                statementExecutor.fillManyToMany(tableInfo, new TableInfo<>(ReflectionUtils.getCollectionGenericClass(field)), field, object);
            }
        }

        return result;
    }

    @Override
    public void update(T object) {

    }

    @Override
    public void delete(T object) {

    }

    @Override
    public List<T> queryForWhere(Where where) throws SQLException {
        List<T> result = (List<T>) statementExecutor.queryForWhere(tableInfo, where);

        for (T object: result) {
            for (Field field : tableInfo.getManyToManyRelations()) {
                statementExecutor.fillManyToMany(tableInfo, new TableInfo<>(ReflectionUtils.getCollectionGenericClass(field)), field, object);
            }
        }
        return result;
    }



    @Override
    public int queryForUpdate(Update update) throws SQLException {
        return statementExecutor.queryForUpdate(tableInfo, update);
    }

    @Override
    public boolean deleteForWhere(Where where) throws SQLException {
        return statementExecutor.deleteForWhere(tableInfo, where);
    }

    @Override
    public QueryBuilder<T> queryBuilder() {
        return new QueryBuilder<>(tableInfo.getTable(), this);
    }

}
