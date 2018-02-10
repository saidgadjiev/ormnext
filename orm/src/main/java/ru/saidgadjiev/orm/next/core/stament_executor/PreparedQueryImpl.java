package ru.saidgadjiev.orm.next.core.stament_executor;

import ru.saidgadjiev.orm.next.core.logger.Log;
import ru.saidgadjiev.orm.next.core.logger.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by said on 12.11.17.
 */
public class PreparedQueryImpl implements IPreparedStatement {

    private PreparedStatement preparedStatement;

    private Log log = LoggerFactory.getLogger(PreparedQueryImpl.class);

    public PreparedQueryImpl(PreparedStatement preparedStatement, String sql) {
        this.preparedStatement = preparedStatement;
        log.debug(sql);
    }

    @Override
    public void setObject(int index, Object value) throws SQLException {
        log.debug("Set " + index + " " + value);
        preparedStatement.setObject(index, value);
    }

    @Override
    public DatabaseResults executeQuery() throws SQLException {
        ResultSet resultSet = preparedStatement.executeQuery();

        return new DatabaseResults(resultSet, resultSet.getMetaData());
    }

    @Override
    public int executeUpdate() throws SQLException {
        return preparedStatement.executeUpdate();
    }

    @Override
    public void addBatch() throws SQLException {
        preparedStatement.addBatch();
    }

    @Override
    public DatabaseResults executeQuery(String sql) throws SQLException {
        ResultSet resultSet = preparedStatement.executeQuery(sql);

        return new DatabaseResults(resultSet, resultSet.getMetaData());
    }

    @Override
    public int executeUpdate(String sql) throws SQLException {
        return preparedStatement.executeUpdate(sql);
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        return preparedStatement.getGeneratedKeys();
    }

    @Override
    public int[] executeBatch() throws SQLException {
        return preparedStatement.executeBatch();
    }

    @Override
    public void close() throws SQLException {
        preparedStatement.close();
    }

}
