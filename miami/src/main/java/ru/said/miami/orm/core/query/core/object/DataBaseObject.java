package ru.said.miami.orm.core.query.core.object;

import ru.said.miami.orm.core.cache.ObjectCache;
import ru.said.miami.orm.core.field.DBFieldType;
import ru.said.miami.orm.core.table.TableInfo;

import java.lang.reflect.Constructor;
import java.util.Optional;

public class DataBaseObject<T> {

    private final ObjectBuilder<T> objectBuilder;

    private final ObjectCreator<T> objectCreator;

    private final TableInfo<T> tableInfo;

    private ObjectCache objectCache;

    public DataBaseObject(TableInfo<T> tableInfo, ObjectCreator<T> objectCreator, ObjectBuilder<T> objectBuilder) {
        this.objectBuilder = objectBuilder;
        this.objectCreator = objectCreator;
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
