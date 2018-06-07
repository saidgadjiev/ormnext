package ru.saidgadjiev.ormnext.core.loader;

import ru.saidgadjiev.ormnext.core.connection.DatabaseResults;
import ru.saidgadjiev.ormnext.core.dao.Session;
import ru.saidgadjiev.ormnext.core.loader.rowreader.entityinitializer.ResultSetValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Temporary result set context.
 * Use for holds read values and created entity objects.
 *
 * @author Said Gadjiev
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
     *
     * @see EntityProcessingState
     */
    private Map<String, Map<Object, EntityProcessingState>> processingStateMap = new HashMap<>();

    /**
     * Temporary object cache.
     */
    private Map<Class<?>, Map<Object, Object>> cache = new HashMap<>();

    /**
     * Cache helper.
     */
    private final CacheHelper cacheHelper;

    /**
     * Create a new instance.
     *
     * @param session         target session
     * @param databaseResults target database results
     * @param cacheHelper     target cache helper
     */
    public ResultSetContext(Session session, DatabaseResults databaseResults, CacheHelper cacheHelper) {
        this.session = session;
        this.databaseResults = databaseResults;
        this.cacheHelper = cacheHelper;
    }

    /**
     * Return session.
     *
     * @return session
     */
    public Session getSession() {
        return session;
    }

    /**
     * Return database results.
     *
     * @return database results
     */
    public DatabaseResults getDatabaseResults() {
        return databaseResults;
    }

    /**
     * Return processing state by uid. If it absent put will be created new processing state.
     *
     * @param uid target uid
     * @param id  target entity object id
     * @return processing state
     */
    public EntityProcessingState getProcessingState(String uid, Object id) {
        Map<Object, EntityProcessingState> entityProcessingStateMap = putIfAbsent(
                processingStateMap,
                uid,
                new HashMap<>()
        );
        EntityProcessingState entityProcessingState = new EntityProcessingState();

        entityProcessingState.setKey(id);

        return putIfAbsent(entityProcessingStateMap, id, entityProcessingState);
    }

    /**
     * Return all processing states associated with requested uid.
     *
     * @param uid target uid
     * @return all processing states
     */
    public Map<Object, EntityProcessingState> getProcessingStates(String uid) {
        return processingStateMap.get(uid);
    }

    /**
     * Put value to map if absent.
     *
     * @param map   target map
     * @param key   target key
     * @param value target value
     * @param <K>   key type
     * @param <V>   value type
     * @return putted value
     */
    private <K, V> V putIfAbsent(Map<K, V> map, K key, V value) {
        V putted = map.putIfAbsent(key, value);

        if (putted == null) {
            return value;
        }

        return putted;
    }

    /**
     * Return cache helper.
     *
     * @return cache helper
     */
    public CacheHelper getCacheHelper() {
        return cacheHelper;
    }

    /**
     * Add new object to cache.
     *
     * @param id   target id
     * @param data target object
     */
    public void addEntry(Object id, Object data) {
        cache.putIfAbsent(data.getClass(), new HashMap<>());
        Map<Object, Object> objectCache = cache.get(data.getClass());

        objectCache.put(id, data);
    }

    /**
     * Retrieve object from temporary cache {@link #cache}.
     *
     * @param tClass target object class
     * @param id     target object id
     * @return object from cache or else null if it not exist
     */
    public Object getEntry(Class<?> tClass, Object id) {
        return cache.get(tClass).get(id);
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
        private List<ResultSetValue> values;

        /**
         * Read collection object ids from result set.
         */
        private List<Object> collectionObjectIds = new ArrayList<>();

        /**
         * Entity key.
         */
        private Object key;

        /**
         * Provide entity instance.
         *
         * @param instance entity instance
         */
        public void setEntityInstance(Object instance) {
            this.entityInstance = instance;
        }

        /**
         * Provide read values.
         *
         * @param values read values
         */
        public void setValuesFromResultSet(List<ResultSetValue> values) {
            this.values = values;
        }

        /**
         * Return entity instance.
         *
         * @return entity instance
         */
        public Object getEntityInstance() {
            return entityInstance;
        }

        /**
         * Return read values.
         *
         * @return read values
         */
        public List<ResultSetValue> getValues() {
            return values;
        }

        /**
         * True if is a new instance.
         *
         * @return true if is a new instance
         */
        public boolean isNew() {
            return isNew;
        }

        /**
         * Provide entity instance is a new.
         *
         * @param aNew entity instance is a new
         */
        public void setNew(boolean aNew) {
            isNew = aNew;
        }

        /**
         * Add a new read collection object id.
         *
         * @param id a new read collection object id
         */
        public void addCollectionObjectId(Object id) {
            collectionObjectIds.add(id);
        }

        /**
         * Return read collection object ids.
         *
         * @return read collection object ids
         */
        public List<Object> getCollectionObjectIds() {
            return collectionObjectIds;
        }

        /**
         * Return entity key.
         *
         * @return entity key
         */
        public Object getKey() {
            return key;
        }

        /**
         * Provide entity key.
         *
         * @param key target entity key
         */
        public void setKey(Object key) {
            this.key = key;
        }
    }
}
