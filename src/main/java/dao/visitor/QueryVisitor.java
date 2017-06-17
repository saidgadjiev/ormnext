package dao.visitor;

import clause.query.InsertQuery;
import clause.table.CreateTable;
import clause.query.UpdateValue;

/**
 * Created by said on 17.06.17.
 */
public interface QueryVisitor {

    void start(InsertQuery insertQuery);

    void start(UpdateValue updateValue);

    void finish(InsertQuery insertQuery);

    void finish(UpdateValue updateValue);

    String preparedQuery();

    void start(CreateTable createTable);
}
