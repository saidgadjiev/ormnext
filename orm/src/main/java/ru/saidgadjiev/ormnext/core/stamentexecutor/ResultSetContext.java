package ru.saidgadjiev.ormnext.core.stamentexecutor;

import ru.saidgadjiev.ormnext.core.dao.InternalSession;
import ru.saidgadjiev.ormnext.core.support.DatabaseResultSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultSetContext {

    private InternalSession dao;

    private DatabaseResultSet databaseResults;

    private Map<String, Map<Object, EntityProcessingState>> processingStateMap = new HashMap<>();

    public ResultSetContext(InternalSession dao, DatabaseResultSet databaseResults) {
        this.dao = dao;
        this.databaseResults = databaseResults;
    }

    public InternalSession getDao() {
        return dao;
    }

    public DatabaseResultSet getDatabaseResults() {
        return databaseResults;
    }

    public EntityProcessingState getProcessingState(String uid, Object id) {
        Map<Object, EntityProcessingState> entityProcessingStateMap = putIfAbsent(processingStateMap, uid, new HashMap<>());

        return putIfAbsent(entityProcessingStateMap, id, new EntityProcessingState());
    }

    public Map<Object, EntityProcessingState> getProcessingStates(String uid) {
        return processingStateMap.get(uid);
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
