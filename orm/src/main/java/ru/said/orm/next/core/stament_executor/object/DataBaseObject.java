package ru.said.orm.next.core.stament_executor.object;

import ru.said.orm.next.core.cache.ObjectCache;
import ru.said.orm.next.core.field.field_type.DBFieldType;
import ru.said.orm.next.core.table.TableInfo;

import javax.sql.DataSource;
import java.util.Optional;

public class DataBaseObject<T> {

    private final ObjectBuilder<T> objectBuilder;

    private final ObjectCreator<T> objectCreator;

    private TableInfo<T> tableInfo;

    private ObjectCache objectCache;

    public DataBaseObject(DataSource dataSource, TableInfo<T> tableInfo) {
        this.objectBuilder = new ObjectBuilder<>(dataSource, tableInfo);
        this.objectCreator = new ObjectCreator<>(dataSource, tableInfo);
        this.tableInfo = tableInfo;
    }

    public Optional<ObjectCache> getObjectCache() {
        return Optional.ofNullable(objectCache);
    }

    public void setObjectCache(ObjectCache objectCache) {
        this.objectCache = objectCache;
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

    public void copy(T srcObject, T destObject) throws Exception {
        for (DBFieldType fieldType : tableInfo.toDBFieldTypes()) {
            fieldType.assign(destObject, fieldType.access(srcObject));
        }
    }
}
