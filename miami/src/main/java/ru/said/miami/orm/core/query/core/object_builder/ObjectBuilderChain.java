package ru.said.miami.orm.core.query.core.object_builder;

import ru.said.miami.orm.core.query.core.IMiamiData;
import ru.said.miami.orm.core.table.TableInfo;

import javax.sql.DataSource;

public class ObjectBuilderChain<T> {

    private ObjectPartBuilder objectPartBuilder;
    private TableInfo<T> tableInfo;

    public ObjectBuilderChain(DataSource dataSource, TableInfo<T> tableInfo) {
        this.tableInfo = tableInfo;
        this.objectPartBuilder = new BaseBuilder(tableInfo);
        objectPartBuilder.linkWith(new ForeignBuilder(tableInfo)).linkWith(new ForeignCollectionBuilder(tableInfo, dataSource));
    }

    public T build(IMiamiData data) throws Exception {
        T object = tableInfo.getConstructor().newInstance();

        objectPartBuilder.check(data, object);

        return object;
    }
}
