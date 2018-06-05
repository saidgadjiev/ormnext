package ru.saidgadjiev.ormnext.core.dao;

import ru.saidgadjiev.ormnext.core.connection.DatabaseConnection;
import ru.saidgadjiev.ormnext.core.connection.DatabaseResults;
import ru.saidgadjiev.ormnext.core.dialect.Dialect;
import ru.saidgadjiev.ormnext.core.field.fieldtype.IDatabaseColumnType;
import ru.saidgadjiev.ormnext.core.loader.Argument;
import ru.saidgadjiev.ormnext.core.loader.GeneratedKey;
import ru.saidgadjiev.ormnext.core.query.visitor.element.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * This interface implement for create new engine {@link SessionManagerBuilder#databaseEngine}.
 * It must be use for execute statements.
 *
 * @param <T> connection type
 * @author said gadjiev
 * @see ru.saidgadjiev.ormnext.core.loader.DefaultEntityLoader
 */
public interface DatabaseEngine<T> {

    /**
     * Execute selectQuery statement.
     *
     * @param connection  target connection
     * @param selectQuery target selectQuery statement
     * @param args        selectQuery statement args
     * @return execute selectQuery results
     * @throws SQLException on any SQL problems
     */
    DatabaseResults select(DatabaseConnection<T> connection, SelectQuery selectQuery, Map<Integer, Argument> args)
            throws SQLException;

    /**
     * Execute insert statement.
     *
     * @param databaseConnection target connection
     * @param createQuery        target insert statement
     * @param args               insert statement args
     * @param primaryKey         primary key column type
     * @param generatedKey       generated key holder
     * @return inserted row count
     * @throws SQLException on any SQL problems
     */
    int create(DatabaseConnection<T> databaseConnection,
               CreateQuery createQuery,
               Map<Integer, Argument> args,
               IDatabaseColumnType primaryKey,
               GeneratedKey generatedKey) throws SQLException;

    /**
     * Execute insert statement for args list.
     *
     * @param databaseConnection target connection
     * @param createQuery        target insert statement
     * @param argList            insert statement args list
     * @param primaryKey         primary key column type
     * @param generatedKeys      generated key holders list
     * @return inserted row count
     * @throws SQLException on any SQL problems
     */
    int create(DatabaseConnection<Connection> databaseConnection,
               CreateQuery createQuery,
               List<Map<Integer, Argument>> argList,
               IDatabaseColumnType primaryKey,
               List<GeneratedKey> generatedKeys) throws SQLException;

    /**
     * Execute batch queries.
     *
     * @param databaseConnection target connection
     * @param sqlStatements      target sql statements
     * @return batch results
     * @throws SQLException any SQL exceptions
     */
    int[] executeBatch(DatabaseConnection<?> databaseConnection,
                       List<SqlStatement> sqlStatements) throws SQLException;

    /**
     * Execute delete statement.
     *
     * @param connection  target connection
     * @param deleteQuery target delete statement
     * @param args        delete statement args
     * @return deleted row count
     * @throws SQLException on any SQL problems
     */
    int delete(DatabaseConnection<T> connection, DeleteQuery deleteQuery, Map<Integer, Argument> args)
            throws SQLException;

    /**
     * Execute update statement.
     *
     * @param connection  target connection
     * @param updateQuery target update statement
     * @param args        update statement args
     * @return updated row count
     * @throws SQLException on any SQL problems
     */
    int update(DatabaseConnection<T> connection, UpdateQuery updateQuery, Map<Integer, Argument> args)
            throws SQLException;

    /**
     * Execute create table statement.
     *
     * @param createTableQuery target create table statement
     * @param connection       target connection
     * @return execute select results
     * @throws SQLException on any SQL problems
     */
    boolean createTable(DatabaseConnection<T> connection, CreateTableQuery createTableQuery)
            throws SQLException;

    /**
     * Execute drop table statement.
     *
     * @param dropTableQuery target drop table statement
     * @param connection     target connection
     * @return true if drop success
     * @throws SQLException on any SQL problems
     */
    boolean dropTable(DatabaseConnection<T> connection, DropTableQuery dropTableQuery)
            throws SQLException;

    /**
     * Execute create index statement.
     *
     * @param createIndexQuery target create index statement
     * @param connection       target connection
     * @throws SQLException on any SQL problems
     */
    void createIndex(DatabaseConnection<T> connection, CreateIndexQuery createIndexQuery)
            throws SQLException;

    /**
     * Execute drop index statement.
     *
     * @param dropIndexQuery target drop index statement
     * @param connection     target connection
     * @throws SQLException on any SQL problems
     */
    void dropIndex(DatabaseConnection<T> connection, DropIndexQuery dropIndexQuery)
            throws SQLException;

    /**
     * Execute query and return results.
     *
     * @param databaseConnection target connection
     * @param query              target query
     * @return database results
     * @throws SQLException any SQL exceptions
     */
    DatabaseResults query(DatabaseConnection<T> databaseConnection, String query) throws SQLException;

    /**
     * Return current dialect.
     * @return dialect
     */
    Dialect getDialect();
}

