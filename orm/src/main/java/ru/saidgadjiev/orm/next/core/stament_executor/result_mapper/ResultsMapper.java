package ru.saidgadjiev.orm.next.core.stament_executor.result_mapper;

import ru.saidgadjiev.orm.next.core.stament_executor.DatabaseResults;
import ru.saidgadjiev.orm.next.core.table.TableInfo;

public interface ResultsMapper<T> {

    T mapResults(DatabaseResults results) throws Exception;
}
