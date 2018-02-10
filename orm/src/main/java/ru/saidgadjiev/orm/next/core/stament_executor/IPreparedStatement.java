package ru.saidgadjiev.orm.next.core.stament_executor;

import java.sql.SQLException;

/**
 * Created by said on 04.02.2018.
 */
public interface IPreparedStatement extends AutoCloseable, IStatement {

    void setObject(int index, Object value) throws SQLException;

    DatabaseResults executeQuery() throws SQLException;

    int executeUpdate() throws SQLException;

    void addBatch() throws SQLException;

}
