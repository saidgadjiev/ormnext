package dao;

import binding.binder.PropertyBinder;
import clause.QueryBuilder;
import clause.Update;
import clause.Where;
import field.FieldWrapper;
import support.MiamiProperties;
import support.TraceDataSource;
import table.TableInfo;
import utils.PlatformUtils;
import utils.ReflectionUtils;
import utils.StatementExecutor;

import javax.sql.DataSource;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by said on 25.02.17.
 */
public class DaoImpl<T> implements Dao<T> {

    private final TableInfo tableInfo;
    private StatementExecutor statementExecutor;

    public DaoImpl(Class<T> dbTable) throws IOException {
        this.tableInfo = new TableInfo(dbTable);

        MiamiProperties properties = new PropertyBinder<MiamiProperties>(System.getProperty("miami.properties")).getProperties(MiamiProperties.class);
        DataSource dataSource = DataSourceFactory.createDataSource(new PlatformUtils().determineDatabaseType("", ""), properties);

        if (properties.trace()) {
            this.statementExecutor = new StatementExecutor(new TraceDataSource(dataSource));
        } else {
            this.statementExecutor = new StatementExecutor(dataSource);
        }
    }

    public void create(T object) throws SQLException {
        statementExecutor.create(object);
    }

    @Override
    public T queryForId(int id) throws SQLException {
        T result = (T) statementExecutor.queryForId(tableInfo, id);
        for (FieldWrapper field: tableInfo.getManyToManyRelations()) {
            statementExecutor.fillManyToMany(tableInfo, new TableInfo(ReflectionUtils.getCollectionGenericClass(field.getField())), field.getField(), result);
        }

        return result;
    }

    @Override
    public List<T> queryForAll() throws SQLException {
        List<T> result = (List<T>) statementExecutor.queryForAll(tableInfo);

        for (T object: result) {
            for (FieldWrapper field : tableInfo.getManyToManyRelations()) {
                statementExecutor.fillManyToMany(tableInfo, new TableInfo(ReflectionUtils.getCollectionGenericClass(field.getField())), field.getField(), object);
            }
        }

        return result;
    }

    @Override
    public List<T> queryForAll(String sql) throws SQLException {
        List<T> result = (List<T>) statementExecutor.queryForAll(tableInfo, sql);

        for (T object: result) {
            for (FieldWrapper field : tableInfo.getManyToManyRelations()) {
                statementExecutor.fillManyToMany(tableInfo, new TableInfo(ReflectionUtils.getCollectionGenericClass(field.getField())), field.getField(), object);
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
            for (FieldWrapper field : tableInfo.getManyToManyRelations()) {
                statementExecutor.fillManyToMany(tableInfo, new TableInfo(ReflectionUtils.getCollectionGenericClass(field.getField())), field.getField(), object);
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
