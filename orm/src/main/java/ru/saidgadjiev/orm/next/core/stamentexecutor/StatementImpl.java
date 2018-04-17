package ru.saidgadjiev.orm.next.core.stamentexecutor;

import ru.saidgadjiev.orm.next.core.logger.Log;
import ru.saidgadjiev.orm.next.core.logger.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by said on 04.02.2018.
 */
public class StatementImpl implements IStatement {

    private Statement statement;

    private Log log = LoggerFactory.getLogger(StatementImpl.class);

    public StatementImpl(Statement statement) {
        this.statement = statement;
    }

    @Override
    public DatabaseResults executeQuery(String sql) throws SQLException {
        log.debug(sql);
        ResultSet resultSet = statement.executeQuery(sql);

        return new DatabaseResults(resultSet, resultSet.getMetaData());
    }

    @Override
    public int executeUpdate(String sql) throws SQLException {
        log.debug(sql);
        return statement.executeUpdate(sql);
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        return statement.getGeneratedKeys();
    }

    @Override
    public int[] executeBatch() throws SQLException {
        return statement.executeBatch();
    }

    @Override
    public void close() throws SQLException {
        statement.close();
    }
}
