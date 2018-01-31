package ru.said.orm.next.core.stament_executor.result_mapper;

import ru.said.orm.next.core.cache.ObjectCache;
import ru.said.orm.next.core.field.field_type.IDBFieldType;
import ru.said.orm.next.core.stament_executor.DatabaseResults;
import ru.said.orm.next.core.stament_executor.object.DataBaseObject;
import ru.said.orm.next.core.table.TableInfo;

public class CachedResultsMapperDecorator<T> implements ResultsMapper<T> {

    private DataBaseObject<T> dataBaseObject;

    private ResultsMapper<T> resultsMapper;

    public CachedResultsMapperDecorator(DataBaseObject<T> dataBaseObject, ResultsMapper<T> resultsMapper) {
        this.dataBaseObject = dataBaseObject;
        this.resultsMapper = resultsMapper;
    }

    @Override
    public T mapResults(DatabaseResults results) throws Exception {
        TableInfo<T> tableInfo = dataBaseObject.getTableInfo();

        if (dataBaseObject.isCaching() && dataBaseObject.getObjectCache().isPresent() && tableInfo.getPrimaryKey().isPresent()) {
            ObjectCache objectCache = dataBaseObject.getObjectCache().get();
            IDBFieldType idbFieldType = tableInfo.getPrimaryKey().get();

            if (results.hasColumn(idbFieldType.getColumnName())) {
                Object id = results.getObject(idbFieldType.getColumnName());

                if (objectCache.contains(tableInfo.getTableClass(), id)) {
                    return objectCache.get(tableInfo.getTableClass(), id);
                }
            }
        }

        return resultsMapper.mapResults(results);
    }
}
