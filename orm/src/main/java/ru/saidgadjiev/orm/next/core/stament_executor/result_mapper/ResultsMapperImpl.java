package ru.saidgadjiev.orm.next.core.stament_executor.result_mapper;

import ru.saidgadjiev.orm.next.core.stament_executor.DatabaseResults;
import ru.saidgadjiev.orm.next.core.stament_executor.object.ObjectBuilder;
import ru.saidgadjiev.orm.next.core.table.TableInfo;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class ResultsMapperImpl<T> implements ResultsMapper<T> {

    private TableInfo<T> tableInfo;

    private Supplier<ObjectBuilder<T>> objectBuilder;

    public ResultsMapperImpl(TableInfo<T> tableInfo, Supplier<ObjectBuilder<T>> objectBuilderFactory) {
        this.tableInfo = tableInfo;
        this.objectBuilder = objectBuilderFactory;
    }

    @Override
    public T mapResults(DatabaseResults results) throws Exception {
        Set<Class<?>> parents = new HashSet<>();

        return objectBuilder
                .get()
                .newObject()
                .buildBase(results, tableInfo.toDBFieldTypes())
                .buildForeign(results, tableInfo.toForeignFieldTypes(), parents)
                .buildForeignCollection(tableInfo.toForeignCollectionFieldTypes(), parents)
                .build();
    }
}
