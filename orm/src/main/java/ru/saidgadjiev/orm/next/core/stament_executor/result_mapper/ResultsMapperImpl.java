package ru.saidgadjiev.orm.next.core.stament_executor.result_mapper;

import ru.saidgadjiev.orm.next.core.stament_executor.DatabaseResults;
import ru.saidgadjiev.orm.next.core.stament_executor.object.ObjectBuilder;
import ru.saidgadjiev.orm.next.core.table.TableInfo;

import java.util.function.Function;
import java.util.function.Supplier;

public class ResultsMapperImpl<T> implements ResultsMapper<T> {

    private TableInfo<T> tableInfo;

    private ObjectBuilder<T> objectBuilder;

    public ResultsMapperImpl(TableInfo<T> tableInfo, ObjectBuilder<T> objectBuilder) {
        this.tableInfo = tableInfo;
        this.objectBuilder = objectBuilder;
    }

    @Override
    public T mapResults(DatabaseResults results) throws Exception {

        return objectBuilder
                .newObject()
                .buildBase(results, tableInfo.toDBFieldTypes())
                .buildForeign(results, tableInfo.toForeignFieldTypes())
                .buildForeignCollection(tableInfo.toForeignCollectionFieldTypes())
                .build();
    }
}
