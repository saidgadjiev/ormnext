package ru.saidgadjiev.ormnext.core.dao;

import ru.saidgadjiev.ormnext.core.connection_source.DatabaseConnection;
import ru.saidgadjiev.ormnext.core.connection_source.DatabaseResults;
import ru.saidgadjiev.ormnext.core.field.field_type.IDatabaseColumnType;
import ru.saidgadjiev.ormnext.core.query.core.*;
import ru.saidgadjiev.ormnext.core.query_element.*;
import ru.saidgadjiev.ormnext.core.loader.Argument;
import ru.saidgadjiev.ormnext.core.loader.GeneratedKey;

import java.sql.SQLException;
import java.util.Map;

/**
 * This interface implement for create new engine {@link SessionManagerBuilder#databaseEngine}.
 * It must be use for execute statements.
 *
 * @param <T> connection type
 * @see ru.saidgadjiev.ormnext.core.loader.DefaultEntityLoader
 */
public interface DatabaseEngine<T> {

    /**
     * Execute select statement.
     *
     * @param connection target connection
     * @param select     target select statement
     * @param args       select statement args
     * @return execute select results
     * @throws SQLException on any SQL problems
     */
    DatabaseResults select(DatabaseConnection<T> connection, Select select, Map<Integer, Argument> args)
            throws SQLException;

    /**
     * Execute insert statement.
     *
     * @param connection   target connection
     * @param createQuery  target insert statement
     * @param args         insert statement args
     * @param primaryKey   primary key column type
     * @param generatedKey generated key holder
     * @throws SQLException on any SQL problems
     */
    void create(DatabaseConnection<T> connection,
                CreateQuery createQuery,
                Map<Integer, Argument> args,
                IDatabaseColumnType primaryKey,
                GeneratedKey generatedKey) throws SQLException;

    /**
     * Execute delete statement.
     *
     * @param deleteQuery target delete statement
     * @param connection  target connection
     * @param args        delete statement args
     * @throws SQLException on any SQL problems
     */
    void delete(DatabaseConnection<T> connection, DeleteQuery deleteQuery, Map<Integer, Argument> args)
            throws SQLException;

    /**
     * Execute update statement.
     *
     * @param updateQuery target update statement
     * @param connection  target connection
     * @param args        update statement args
     * @throws SQLException on any SQL problems
     */
    void update(DatabaseConnection<T> connection, UpdateQuery updateQuery, Map<Integer, Argument> args)
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
}

