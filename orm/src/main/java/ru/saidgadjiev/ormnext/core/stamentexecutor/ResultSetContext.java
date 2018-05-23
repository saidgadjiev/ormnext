package ru.saidgadjiev.ormnext.core.stamentexecutor;

import ru.saidgadjiev.ormnext.core.dao.Session;
import ru.saidgadjiev.ormnext.core.support.DatabaseResults;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultSetContext {

    private Session dao;

    private DatabaseResults databaseResults;

    private Map<String, Map<Object, EntityProcessingState>> processingStateMap = new HashMap<>();

    private Map<Class<?>, Map<Object, Object>> cache = new HashMap<>();

    public ResultSetContext(Session dao, DatabaseResults databaseResults) {
        this.dao = dao;
        this.databaseResults = databaseResults;
    }

    public Session getDao() {
        return dao;
    }

    public DatabaseResults getDatabaseResults() {
        return databaseResults;
    }

    public EntityProcessingState getProcessingState(String uid, Object id) {
        Map<Object, EntityProcessingState> entityProcessingStateMap = putIfAbsent(processingStateMap, uid, new HashMap<>());

        return putIfAbsent(entityProcessingStateMap, id, new EntityProcessingState());
    }

    public Map<Object, EntityProcessingState> getProcessingStates(String uid) {
        return processingStateMap.get(uid);
    }

    public void addEntry(Object id, Object data) {
        Map<Object, Object> objectCache = putIfAbsent(cache, data.getClass(), new HashMap<>());

        objectCache.put(id, data);
    }

    public Object getEntry(Class<?> tClass, Object id) {
        return cache.get(tClass).get(id);
    }

    private<K, V> V putIfAbsent(Map<K, V> map, K key, V value) {
        V putted = map.putIfAbsent(key, value);

        if (putted == null) {
            return value;
        }

        return putted;
    }

    public static class EntityProcessingState {

        private boolean isNew;

        private Object entityInstance;

        private List<Object> values;

        private List<Object> collectionObjectIds = new ArrayList<>();

        public void setEntityInstance(Object instance) {
            this.entityInstance = instance;
        }

        public void setValuesFromResultSet(List<Object> values) {
            this.values = values;
        }

        public Object getEntityInstance() {
            return entityInstance;
        }

        public List<Object> getValues() {
            return values;
        }

        public boolean isNew() {
            return isNew;
        }

        public void setNew(boolean aNew) {
            isNew = aNew;
        }

        public void addCollectionObjectId(Object id) {
            collectionObjectIds.add(id);
        }

        public List<Object> getCollectionObjectIds() {
            return collectionObjectIds;
        }
    }
}
