package ru.saidgadjiev.ormnext.core.dao;

import ru.saidgadjiev.ormnext.core.query.core.*;
import ru.saidgadjiev.ormnext.core.stamentexecutor.Argument;
import ru.saidgadjiev.ormnext.core.support.DatabaseConnection;
import ru.saidgadjiev.ormnext.core.support.DatabaseResultSet;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public interface DatabaseEngine {

    void select(DatabaseConnection<?> connection, Select select, Collection<Argument> args, ResultSetProcessor resultSetProcessor) throws SQLException;

    void create(DatabaseConnection<?> connection, CreateQuery createQuery, Collection<Argument> args, ResultSetProcessor resultSetProcessor) throws SQLException;

    void delete(DatabaseConnection<?> connection, DeleteQuery deleteQuery, Collection<Argument> args) throws SQLException;

    void update(DatabaseConnection<?> connection, UpdateQuery updateQuery, Collection<Argument> args) throws SQLException;

    boolean createTable(DatabaseConnection<?> connection, CreateTableQuery createTableQuery) throws SQLException;

    boolean dropTable(DatabaseConnection<?> connection, DropTableQuery dropTableQuery) throws SQLException;

    void createIndexes(DatabaseConnection<?> connection, Iterator<CreateIndexQuery> indexQueryIterator) throws SQLException;

    void dropIndexes(DatabaseConnection<?> connection, Iterator<DropIndexQuery> dropIndexQueryIterator) throws SQLException;

    interface ResultSetProcessor {
        void process(DatabaseResultSet resultSetObject) throws SQLException;
    }
}
