package ru.said.miami.orm.core.query.core.query_builder;

import ru.said.miami.orm.core.query.core.IMiamiCollection;
import ru.said.miami.orm.core.query.core.IMiamiData;
import ru.said.miami.orm.core.query.core.SelectQuery;
import ru.said.miami.orm.core.query.core.object.DataBaseObject;
import ru.said.miami.orm.core.query.core.object.ObjectBuilder;
import ru.said.miami.orm.core.table.TableInfo;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class QueryBuilder<T> {

    private Where where;

    private DataSource dataSource;

    private DataBaseObject<T> dataBaseObject;

    public QueryBuilder(DataSource dataSource, DataBaseObject<T> dataBaseObject) {
        this.dataSource = dataSource;
        this.dataBaseObject = dataBaseObject;
        this.where = new Where();
    }

    public Where where() {
        return where;
    }

    public List<T> execute() throws SQLException {
        List<T> resultList = new ArrayList<>();
        SelectQuery query = SelectQuery.buildQueryForAll(dataBaseObject.getTableInfo().getTableName());

        query.setWhere(where.getWhere());
        try (Connection connection = dataSource.getConnection()) {
            try (IMiamiCollection result = query.execute(connection)) {
                while (result.next()) {
                    IMiamiData data = result.get();

                    resultList.add(
                            dataBaseObject.getObjectBuilder().newObject()
                            .buildBase(data)
                            .buildForeign(data)
                            .buildForeignCollection(data)
                            .build()
                    );
                }
            }
        } catch (Exception ex) {
            throw new SQLException(ex);
        }

        return resultList;
    }

}
