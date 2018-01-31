package ru.said.orm.next.core.stament_executor.result_mapper;

import ru.said.orm.next.core.stament_executor.DatabaseResults;
import ru.said.orm.next.core.stament_executor.object.DataBaseObject;
import ru.said.orm.next.core.table.TableInfo;

public class ResultsMapperImpl<T> implements ResultsMapper<T> {

    private DataBaseObject<T> dataBaseObject;

    public ResultsMapperImpl(DataBaseObject<T> dataBaseObject) {
        this.dataBaseObject = dataBaseObject;
    }

    @Override
    public T mapResults(DatabaseResults results) throws Exception {
        TableInfo<T> tableInfo = dataBaseObject.getTableInfo();

        return dataBaseObject
                .getObjectBuilder()
                .newObject()
                .buildBase(results, tableInfo.toDBFieldTypes())
                .buildForeign(results, tableInfo.toForeignFieldTypes())
                .buildForeignCollection(tableInfo.toForeignCollectionFieldTypes())
                .build();
    }
}
