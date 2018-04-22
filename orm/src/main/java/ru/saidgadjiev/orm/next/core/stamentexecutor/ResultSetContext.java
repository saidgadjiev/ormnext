package ru.saidgadjiev.orm.next.core.stamentexecutor;

import ru.saidgadjiev.orm.next.core.dao.Session;

import java.sql.ResultSet;
import java.util.*;
import java.util.function.Function;

public class ResultSetContext {

    private Session session;

    private ResultSet databaseResults;

    private Map<String, Map<Object, EntityProcessingState>> processingStateMap = new HashMap<>();

    public ResultSetContext(Session session, ResultSet databaseResults) {
        this.session = session;
        this.databaseResults = databaseResults;
    }

    public Session getSession() {
        return session;
    }

    public ResultSet getDatabaseResults() {
        return databaseResults;
    }

    public EntityProcessingState getProcessingState(String uid, Object id) {
        processingStateMap.putIfAbsent(uid, new HashMap<>());
        processingStateMap.get(uid).putIfAbsent(id, new EntityProcessingState());

        return processingStateMap.get(uid).get(id);
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
