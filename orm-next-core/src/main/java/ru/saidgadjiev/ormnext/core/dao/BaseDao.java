package ru.saidgadjiev.ormnext.core.dao;

import ru.saidgadjiev.ormnext.core.connection_source.DatabaseResults;
import ru.saidgadjiev.ormnext.core.query.criteria.impl.CriteriaQuery;

import java.sql.SQLException;
import java.util.List;

/**
 * The definition of the Database Access Objects that handle the reading and writing a class from a database.
 */
public interface BaseDao {

    /**
     * Create new row in the database from an object.
     *
     * @param object object that we are creating in the database
     * @param <T>    object class that the code will be operating on
     * @throws SQLException on any SQL problems
     */
    <T> void create(T object) throws SQLException;

    /**
     * Create table in the database.
     *
     * @param tClass     target table class
     * @param ifNotExist append if not exist part if true
     * @param <T>        table class that the code will be operating on
     * @return true if table created or false
     * @throws SQLException on any SQL problems
     */
    <T> boolean createTable(Class<T> tClass, boolean ifNotExist) throws SQLException;

    /**
     * Retrieves an object associated with id.
     *
     * @param tClass target table class
     * @param id     identifier that matches a specific row in the database to find and return.
     * @param <T>    table class type
     * @param <ID>   id type
     * @return object that associated with requested id
     * @throws SQLException on any SQL problems
     */
    <T, ID> T queryForId(Class<T> tClass, ID id) throws SQLException;

    /**
     * Retrieves all objects from database table.
     *
     * @param tClass target table class
     * @param <T>    table class type
     * @return A list of all of the objects in the table
     * @throws SQLException on any SQL problems
     */
    <T> List<T> queryForAll(Class<T> tClass) throws SQLException;

    /**
     * Update a database row associated with id from requested object.
     *
     * @param object target object
     * @param <T>    object type
     * @throws SQLException on any SQL problems
     */
    <T> void update(T object) throws SQLException;

    /**
     * Remove a database row associated with id from requested object.
     *
     * @param object target object
     * @param <T>    object type
     * @throws SQLException on any SQL problems
     */
    <T> void delete(T object) throws SQLException;

    /**
     * Remove a database row associated with requested id from requested table.
     *
     * @param tClass target table class
     * @param id     id
     * @param <T>    table class type
     * @param <ID>   id type
     * @throws SQLException on any SQL problems
     */
    <T, ID> void deleteById(Class<T> tClass, ID id) throws SQLException;

    /**
     * Drop requested table from the database.
     *
     * @param tClass  target table class
     * @param ifExist append if exist part if true
     * @param <T>     table class type
     * @return true if drop is success or false
     * @throws SQLException on any SQL problems
     */
    <T> boolean dropTable(Class<T> tClass, boolean ifExist) throws SQLException;

    /**
     * Create indexes by a requested table.
     *
     * @param tClass target table class
     * @param <T>    table class type
     * @throws SQLException on any SQL problems
     */
    <T> void createIndexes(Class<T> tClass) throws SQLException;

    /**
     * Drop requested table indexes.
     *
     * @param tClass target table class
     * @param <T>    table class type
     * @throws SQLException on any SQL problems
     */
    <T> void dropIndexes(Class<T> tClass) throws SQLException;

    /**
     * Count star in requested table.
     *
     * @param tClass target table class
     * @param <T>    table class type
     * @return entities count in requested table
     * @throws SQLException on any SQL problems
     */
    <T> long countOff(Class<T> tClass) throws SQLException;

    /**
     * Query for the items in the object table which match the criteria query.
     *
     * @param criteriaQuery target criteria query
     * @param <T>           object type
     * @return a list of all of the objects in the table that match the query.
     * @throws SQLException on any SQL problems
     * @see CriteriaQuery
     */
    <T> List<T> list(CriteriaQuery<T> criteriaQuery) throws SQLException;

    /**
     * Query for aggregate functions which retrieve one long value.
     *
     * @param criteriaQuery target query
     * @param <T>           object type
     * @return long value which return query
     * @throws SQLException on any SQL problems
     * @see CriteriaQuery
     */
    <T> long queryForLong(CriteriaQuery<T> criteriaQuery) throws SQLException;

    /**
     * Execute query and return results.
     *
     * @param query target query
     * @return database results
     * @throws SQLException any SQL exceptions
     */
    DatabaseResults query(String query) throws SQLException;
}
