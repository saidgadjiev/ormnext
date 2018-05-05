package ru.saidgadjiev.ormnext.core.stamentexecutor;

import ru.saidgadjiev.ormnext.core.dao.Dao;
import ru.saidgadjiev.ormnext.core.dao.InternalDao;
import ru.saidgadjiev.ormnext.core.support.OrmNextResultSet;
import ru.saidgadjiev.ormnext.core.dao.InternalDao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultSetContext {

    private InternalDao dao;

    private OrmNextResultSet databaseResults;

    private Map<String, Map<Object, EntityProcessingState>> processingStateMap = new HashMap<>();

    private Map<Class<?>, Map<Object, Object>> cache = new HashMap<>();

    public ResultSetContext(InternalDao dao, OrmNextResultSet databaseResults) {
        this.dao = dao;
        this.databaseResults = databaseResults;
    }

    public InternalDao getDao() {
        return dao;
    }

    public OrmNextResultSet getDatabaseResults() {
        return databaseResults;
    }

    public EntityProcessingState getProcessingState(String uid, Object id) {
        processingStateMap.putIfAbsent(uid, new HashMap<>());
        processingStateMap.get(uid).putIfAbsent(id, new EntityProcessingState());

        return processingStateMap.get(uid).get(id);
    }

    public void addEntry(Object id, Object data) {
        cache.putIfAbsent(data.getClass(), new HashMap<>());
        Map<Object, Object> objectCache = cache.get(data.getClass());

        objectCache.put(id, data);
    }

    public Object getEntry(Class<?> tClass, Object id) {
        return cache.get(tClass).get(id);
    }

    public static class EntityProcessingState {

        private boolean isNew;

        private Object entityInstance;

        private List<Object> values;

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
    }
}
