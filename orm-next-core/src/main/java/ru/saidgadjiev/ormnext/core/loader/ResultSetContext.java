package ru.saidgadjiev.ormnext.core.loader;

import ru.saidgadjiev.ormnext.core.dao.Session;
import ru.saidgadjiev.ormnext.core.connection_source.DatabaseResults;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Temporary result set context.
 * Use for holds read values and created entity objects.
 */
public class ResultSetContext {

    /**
     * Session object.
     */
    private final Session session;

    /**
     * Database results.
     */
    private DatabaseResults databaseResults;

    /**
     * Map for associate uid with processing state.
     * @see EntityProcessingState
     */
    private Map<String, Map<Object, EntityProcessingState>> processingStateMap = new HashMap<>();

    /**
     * Create a new instance.
     * @param session target session
     * @param databaseResults target database results
     */
    public ResultSetContext(Session session, DatabaseResults databaseResults) {
        this.session = session;
        this.databaseResults = databaseResults;
    }

    /**
     * Return session.
     * @return session
     */
    public Session getSession() {
        return session;
    }

    /**
     * Return database results.
     * @return database results
     */
    public DatabaseResults getDatabaseResults() {
        return databaseResults;
    }

    /**
     * Return processing state by uid. If it absent put will be created new processing state.
     * @param uid target uid
     * @param id target entity object id
     * @return processing state
     */
    public EntityProcessingState getProcessingState(String uid, Object id) {
        Map<Object, EntityProcessingState> entityProcessingStateMap = putIfAbsent(
                processingStateMap,
                uid,
                new HashMap<>()
        );

        return putIfAbsent(entityProcessingStateMap, id, new EntityProcessingState());
    }

    /**
     * Return all processing states associated with requested uid.
     * @param uid target uid
     * @return all processing states
     */
    public Map<Object, EntityProcessingState> getProcessingStates(String uid) {
        return processingStateMap.get(uid);
    }

    /**
     * Put value to map if absent.
     * @param map target map
     * @param key target key
     * @param value target value
     * @param <K> key type
     * @param <V> value type
     * @return putted value
     */
    private<K, V> V putIfAbsent(Map<K, V> map, K key, V value) {
        V putted = map.putIfAbsent(key, value);

        if (putted == null) {
            return value;
        }

        return putted;
    }

    /**
     * Entity processing state. Temporary class which holds read from result set values, collection object ids,
     * entity instance and is new entity instance or already read.
     */
    public static class EntityProcessingState {

        /**
         * True if entity instance is new.
         */
        private boolean isNew;

        /**
         * Entity instance.
         */
        private Object entityInstance;

        /**
         * Read values from result set.
         */
        private List<Object> values;

        /**
         * Read collection object ids from result set.
         */
        private List<Object> collectionObjectIds = new ArrayList<>();

        /**
         * Provide entity instance.
         * @param instance entity instance
         */
        public void setEntityInstance(Object instance) {
            this.entityInstance = instance;
        }

        /**
         * Provide read values.
         * @param values read values
         */
        public void setValuesFromResultSet(List<Object> values) {
            this.values = values;
        }

        /**
         * Return entity instance.
         * @return entity instance
         */
        public Object getEntityInstance() {
            return entityInstance;
        }

        /**
         * Return read values.
         * @return read values
         */
        public List<Object> getValues() {
            return values;
        }

        /**
         * True if is a new instance.
         * @return true if is a new instance
         */
        public boolean isNew() {
            return isNew;
        }

        /**
         * Provide entity instance is a new.
         * @param aNew entity instance is a new
         */
        public void setNew(boolean aNew) {
            isNew = aNew;
        }

        /**
         * Add a new read collection object id.
         * @param id  a new read collection object id
         */
        public void addCollectionObjectId(Object id) {
            collectionObjectIds.add(id);
        }

        /**
         * Return read collection object ids.
         * @return read collection object ids
         */
        public List<Object> getCollectionObjectIds() {
            return collectionObjectIds;
        }
    }
}
