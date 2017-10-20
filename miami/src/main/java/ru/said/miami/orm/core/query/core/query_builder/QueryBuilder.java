package ru.said.miami.orm.core.query.core.query_builder;

import ru.said.miami.orm.core.query.core.IMiamiCollection;
import ru.said.miami.orm.core.query.core.IMiamiData;
import ru.said.miami.orm.core.query.core.Query;
import ru.said.miami.orm.core.query.core.SelectQuery;
import ru.said.miami.orm.core.query.core.object.DataBaseObject;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class QueryBuilder<T> implements IPreparedQuery<List<T>> {

    private Where<List<T>> where;

    private SelectQuery selectQuery;

    private DataBaseObject<T> dataBaseObject;

    public QueryBuilder(DataBaseObject<T> dataBaseObject) {
        this.dataBaseObject = dataBaseObject;
        this.selectQuery = SelectQuery.buildQueryForAll(dataBaseObject.getTableInfo().getTableName());
        this.where = new Where<>(this);
    }

    public Where<List<T>> where() {
        return where;
    }

    @Override
    public Query<List<T>> prepare() {
        selectQuery.setWhere(where.getWhere());

        return connection -> {
            Statement statement = connection.createStatement();
            List<T> resultList = new ArrayList<>();

            try (ResultSet resultSet = statement.executeQuery(selectQuery.query())) {
                while (resultSet.next()) {
                    IMiamiData data = new IMiamiData() {
                        @Override
                        public boolean getBoolean(String name) throws SQLException {
                            return resultSet.getBoolean(name);
                        }

                        @Override
                        public int getInt(String name) throws SQLException {
                            return resultSet.getInt(name);
                        }

                        @Override
                        public String getString(String name) throws SQLException {
                            return resultSet.getString(name);
                        }

                        @Override
                        public Date getTime(String name) throws SQLException {
                            return resultSet.getTime(name);
                        }

                        @Override
                        public double getDouble(String name) throws SQLException {
                            return resultSet.getDouble(name);
                        }

                        @Override
                        public Object getObject(String name) throws SQLException {
                            return resultSet.getObject(name);
                        }
                    };
                    try {
                        resultList.add(dataBaseObject.getObjectBuilder().newObject().buildBase(data).buildForeign(data).buildForeignCollection().build());
                    } catch (Exception ex) {
                        throw new SQLException(ex);
                    }
                }

                return resultList;
            }
        };
    }
}
