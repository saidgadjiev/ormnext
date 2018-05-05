package ru.saidgadjiev.ormnext.core.stamentexecutor.rowreader;

import ru.saidgadjiev.ormnext.core.stamentexecutor.ResultSetContext;

import java.sql.SQLException;

public interface RowReader {

    RowResult<Object> startRead(ResultSetContext resultSetContext) throws SQLException;

    void finishRead(ResultSetContext resultSetContext) throws SQLException;
}
