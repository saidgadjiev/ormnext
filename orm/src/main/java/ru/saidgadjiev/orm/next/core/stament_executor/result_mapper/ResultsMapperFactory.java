package ru.saidgadjiev.orm.next.core.stament_executor.result_mapper;

import ru.saidgadjiev.orm.next.core.cache.CacheContext;
import ru.saidgadjiev.orm.next.core.support.ConnectionSource;
import ru.saidgadjiev.orm.next.core.table.TableInfo;

import java.util.HashSet;
import java.util.Map;

public class ResultsMapperFactory {

    private ConnectionSource dataSource;

    private CacheContext cacheContext;

    public ResultsMapperFactory(ConnectionSource dataSource, CacheContext cacheContext) {
        this.dataSource = dataSource;
        this.cacheContext = cacheContext;
    }

    public ResultsMapper<?> createResultsMapper(TableInfo<?> tableInfo, Map<String, String> columnsAliasMap) {
        return new CachedResultsMapperDecorator<>(
                tableInfo,
                cacheContext,
                new ResultsMapperImpl<>(dataSource, tableInfo, columnsAliasMap, new HashSet<>()));
    }
}
