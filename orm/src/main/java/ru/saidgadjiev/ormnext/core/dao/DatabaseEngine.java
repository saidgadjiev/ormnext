package ru.saidgadjiev.ormnext.core.dao;

import ru.saidgadjiev.ormnext.core.query.core.*;
import ru.saidgadjiev.ormnext.core.stamentexecutor.Argument;
import ru.saidgadjiev.ormnext.core.support.DatabaseConnection;
import ru.saidgadjiev.ormnext.core.support.DatabaseResults;

import java.sql.SQLException;
import java.util.Map;

/**
 * This interface implement for create new engine {@link SessionManagerBuilder#databaseEngine}.
 * It must be use for execute statements.
 * @see ru.saidgadjiev.ormnext.core.stamentexecutor.DefaultEntityLoader
 */
public interface DatabaseEngine {

    /**
     * Execute select statement.
     * @param connection target connection
     * @param select target select statement
     * @param args select statement args
     * @return execute select results
     * @throws SQLException on any SQL problems
     */
    DatabaseResults select(DatabaseConnection<?> connection, Select select, Map<Integer, Argument> args)
            throws SQLException;

    /**
     * Execute insert statement.
     * @param createQuery target insert statement
     * @param connection target connection
     * @param args insert statement args
     * @return generated key results
     * @throws SQLException on any SQL problems
     */
    DatabaseResults create(DatabaseConnection<?> connection, CreateQuery createQuery, Map<Integer, Argument> args)
            throws SQLException;

    /**
     * Execute delete statement.
     * @param deleteQuery target delete statement
     * @param connection target connection
     * @param args delete statement args
     * @throws SQLException on any SQL problems
     */
    void delete(DatabaseConnection<?> connection, DeleteQuery deleteQuery, Map<Integer, Argument> args)
            throws SQLException;

    /**
     * Execute update statement.
     * @param updateQuery target update statement
     * @param connection target connection
     * @param args update statement args
     * @throws SQLException on any SQL problems
     */
    void update(DatabaseConnection<?> connection, UpdateQuery updateQuery, Map<Integer, Argument> args)
            throws SQLException;

    /**
     * Execute create table statement.
     * @param createTableQuery target create table statement
     * @param connection target connection
     * @return execute select results
     * @throws SQLException on any SQL problems
     */
    boolean createTable(DatabaseConnection<?> connection, CreateTableQuery createTableQuery)
            throws SQLException;

    /**
     * Execute drop table statement.
     * @param dropTableQuery target drop table statement
     * @param connection target connection
     * @return true if drop success
     * @throws SQLException on any SQL problems
     */
    boolean dropTable(DatabaseConnection<?> connection, DropTableQuery dropTableQuery)
            throws SQLException;

    /**
     * Execute create index statement.
     * @param createIndexQuery target create index statement
     * @param connection target connection
     * @throws SQLException on any SQL problems
     */
    void createIndex(DatabaseConnection<?> connection, CreateIndexQuery createIndexQuery)
            throws SQLException;

    /**
     * Execute drop index statement.
     * @param dropIndexQuery target drop index statement
     * @param connection target connection
     * @throws SQLException on any SQL problems
     */
    void dropIndex(DatabaseConnection<?> connection, DropIndexQuery dropIndexQuery)
            throws SQLException;
}

