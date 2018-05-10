package ru.saidgadjiev.orm.next.core.dao;

import ru.saidgadjiev.orm.next.core.query.core.*;
import ru.saidgadjiev.orm.next.core.support.DatabaseConnection;
import ru.saidgadjiev.orm.next.core.support.OrmNextResultSet;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

public interface DatabaseEngine {
    void select(DatabaseConnection connection, Select select, List<Object> args, DefaultDatabaseEngine.ResultSetProcessor resultSetProcessor) throws SQLException;

    int create(DatabaseConnection databaseConnection, CreateQuery createQuery, List<Object> args, ResultSetProcessor resultSetProcessor) throws SQLException;

    int delete(DatabaseConnection connection, DeleteQuery deleteQuery, List<Object> args) throws SQLException;

    int update(DatabaseConnection connection, UpdateQuery updateQuery, List<Object> args) throws SQLException;

    boolean createTable(DatabaseConnection databaseConnection, CreateTableQuery createTableQuery) throws SQLException;

    boolean dropTable(DatabaseConnection databaseConnection, DropTableQuery dropTableQuery) throws SQLException;

    void createIndexes(DatabaseConnection databaseConnection, Iterator<CreateIndexQuery> indexQueryIterator) throws SQLException;

    void dropIndexes(DatabaseConnection databaseConnection, Iterator<DropIndexQuery> dropIndexQueryIterator) throws SQLException;

    interface ResultSetProcessor {
        void process(OrmNextResultSet resultSetObject) throws SQLException;
    }
}
