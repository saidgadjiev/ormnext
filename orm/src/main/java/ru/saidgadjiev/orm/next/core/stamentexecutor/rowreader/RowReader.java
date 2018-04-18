package ru.saidgadjiev.orm.next.core.stamentexecutor.rowreader;

import ru.saidgadjiev.orm.next.core.stamentexecutor.DatabaseResults;
import ru.saidgadjiev.orm.next.core.stamentexecutor.ResultSetContext;

import java.sql.SQLException;

public interface RowReader {

    Object startRead(ResultSetContext resultSetContext) throws SQLException;

    void finishRead(ResultSetContext resultSetContext) throws SQLException;
}
