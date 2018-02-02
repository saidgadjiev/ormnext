package ru.saidgadjiev.orm.next.core.stament_executor;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Created by said on 12.11.17.
 */
public class GeneratedKeys implements AutoCloseable {

    private ResultSet resultSet;

    private ResultSetMetaData resultSetMetaData;

    private static final Integer GENERATED_CLUMN_INDEX = 1;

    public GeneratedKeys(ResultSet resultSet, ResultSetMetaData metaData) {
        this.resultSet = resultSet;
        this.resultSetMetaData = metaData;
    }

    public Number getGeneratedKey() throws SQLException {
        return getIdColumnData();
    }

    private Number getIdColumnData() throws SQLException {
        int typeVal = resultSetMetaData.getColumnType(GENERATED_CLUMN_INDEX);

        switch (typeVal) {
            case Types.BIGINT :
            case Types.DECIMAL :
            case Types.NUMERIC :
                return resultSet.getLong(GENERATED_CLUMN_INDEX);
            case Types.INTEGER :
                return resultSet.getInt(GENERATED_CLUMN_INDEX);
            default :
                throw new SQLException("Unknown DataType for typeVal " + typeVal + " in column " + GENERATED_CLUMN_INDEX);
        }
    }

    public void close() throws SQLException {
        resultSet.close();
    }

    public boolean next() throws SQLException {
        return resultSet.next();
    }
}
