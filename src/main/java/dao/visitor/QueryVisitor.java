package dao.visitor;

import clause.query.CreateQuery;
import clause.table.CreateTable;
import clause.query.UpdateValue;

/**
 * Created by said on 17.06.17.
 */
public interface QueryVisitor {

    void start(CreateQuery createQuery);

    void start(UpdateValue updateValue);

    void finish(CreateQuery createQuery);

    void finish(UpdateValue updateValue);

    String preparedQuery();

    void start(CreateTable createTable);
}
