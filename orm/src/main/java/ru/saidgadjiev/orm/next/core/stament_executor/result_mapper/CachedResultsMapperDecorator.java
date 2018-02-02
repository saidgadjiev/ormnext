package ru.saidgadjiev.orm.next.core.stament_executor.result_mapper;

import ru.saidgadjiev.orm.next.core.cache.CacheContext;
import ru.saidgadjiev.orm.next.core.cache.ObjectCache;
import ru.saidgadjiev.orm.next.core.field.field_type.IDBFieldType;
import ru.saidgadjiev.orm.next.core.stament_executor.DatabaseResults;
import ru.saidgadjiev.orm.next.core.table.TableInfo;

public class CachedResultsMapperDecorator<T> implements ResultsMapper<T> {

    private final TableInfo<T> tableInfo;

    private final CacheContext cacheContext;

    private ResultsMapper<T> resultsMapper;

    public CachedResultsMapperDecorator(TableInfo<T> tableInfo, CacheContext cacheContext, ResultsMapper<T> resultsMapper) {
        this.tableInfo = tableInfo;
        this.cacheContext = cacheContext;
        this.resultsMapper = resultsMapper;
    }

    @Override
    public T mapResults(DatabaseResults results) throws Exception {
        if (cacheContext.isCaching() && cacheContext.getObjectCache().isPresent() && tableInfo.getPrimaryKey().isPresent()) {
            ObjectCache objectCache = cacheContext.getObjectCache().get();
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
