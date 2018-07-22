package ru.saidgadjiev.ormnext.core.loader.rowreader;

import ru.saidgadjiev.ormnext.core.loader.ResultSetContext;

import java.sql.SQLException;

/**
 * Database result row reader. Read entity object from database result row.
 * It create entity object with two phases algorithm.
 * It mean we read all retrieved entity column values from result set object and save them to
 * temporary context {@link ResultSetContext} it is a first phase.
 * Then we set read values from first phase to entity object it is a second phase.
 *
 * @author Said Gadjiev
 */
public interface RowReader {

    /**
     * Start read. First phase.
     * @param resultSetContext current resultset context
     * @return row read result
     * @throws SQLException any SQL exceptions
     * @see RowResult
     */
    RowResult startRead(ResultSetContext resultSetContext) throws SQLException;

    /**
     * Finish read. Second phase.
     * @param resultSetContext current resultset context
     * @throws SQLException any SQL exceptions
     */
    void finishRead(ResultSetContext resultSetContext) throws SQLException;
}
