package ru.saidgadjiev.orm.next.core.stamentexecutor.rowreader;

import ru.saidgadjiev.orm.next.core.stamentexecutor.DatabaseResults;

public interface RowReader {

    void startRead(DatabaseResults results);

    void finishRead(DatabaseResults results);
}
