package ru.said.miami.orm.core.query.core.object;

import ru.said.miami.orm.core.table.TableInfo;

public class DataBaseObject<T> {

    private final ObjectBuilder<T> objectBuilder;
    private final ObjectCreator<T> objectCreator;
    private final TableInfo<T> tableInfo;

    public DataBaseObject(TableInfo<T> tableInfo, ObjectCreator<T> objectCreator, ObjectBuilder<T> objectBuilder) {
        this.objectBuilder = objectBuilder;
        this.objectCreator = objectCreator;
        this.tableInfo = tableInfo;
    }

    public TableInfo<T> getTableInfo() {
        return tableInfo;
    }

    public ObjectBuilder<T> getObjectBuilder() {
        return objectBuilder;
    }

    public ObjectCreator<T> getObjectCreator() {
        return objectCreator;
    }
}
