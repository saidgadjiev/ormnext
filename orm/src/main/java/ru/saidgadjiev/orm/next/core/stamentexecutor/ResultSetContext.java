package ru.saidgadjiev.orm.next.core.stamentexecutor;

import ru.saidgadjiev.orm.next.core.dao.Session;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultSetContext {

    private Session session;

    private DatabaseResults databaseResults;

    private Map<String, EntityProcessingState> processingStateMap = new HashMap<>();

    public ResultSetContext(Session session, DatabaseResults databaseResults) {
        this.session = session;
        this.databaseResults = databaseResults;
    }

    public Session getSession() {
        return session;
    }

    public DatabaseResults getDatabaseResults() {
        return databaseResults;
    }

    public EntityProcessingState getProcessingState(String uid) {
        processingStateMap.putIfAbsent(uid, new EntityProcessingState());

        return processingStateMap.get(uid);
    }

    public static class EntityProcessingState {

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
    }
}
