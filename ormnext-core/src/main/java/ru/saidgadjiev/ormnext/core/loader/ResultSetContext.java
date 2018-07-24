package ru.saidgadjiev.ormnext.core.loader;

import ru.saidgadjiev.ormnext.core.connection.DatabaseResults;
import ru.saidgadjiev.ormnext.core.dao.CacheSessionManager;
import ru.saidgadjiev.ormnext.core.dao.Session;
import ru.saidgadjiev.ormnext.core.loader.rowreader.ResultSetRow;
import ru.saidgadjiev.ormnext.core.loader.rowreader.ResultSetValue;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

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
     * Resultset columns.
     */
    private Set<String> resultColumns;

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

    private ResultSetRow currentRow;

    /**
     * Create a new instance.
     *
     * @param session         target session
     * @param databaseResults target database results
     */
    public ResultSetContext(Session session, DatabaseResults databaseResults) throws SQLException {
        this.session = session;
        this.databaseResults = databaseResults;

        if (resultColumns == null) {
            resultColumns = databaseResults.getMetaData().getResultColumnNames()
                    .stream()
                    .map(String::toLowerCase)
                    .collect(Collectors.toSet());
        }

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
    public EntityProcessingState getOrCreateProcessingState(String uid, Object id) {
        Map<Object, EntityProcessingState> entityProcessingStateMap = putIfAbsent(
                processingStateMap,
                uid,
                new HashMap<>()
        );
        EntityProcessingState entityProcessingState = new EntityProcessingState();

        entityProcessingState.setKey(id);

        return putIfAbsent(entityProcessingStateMap, id, entityProcessingState);
    }

    public EntityProcessingState getProcessingState(String uid, Object id) {
        Map<Object, EntityProcessingState> entityProcessingStateMap = processingStateMap.get(uid);

        return entityProcessingStateMap == null ? null : entityProcessingStateMap.get(id);
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

    public ResultSetRow getCurrentRow() {
        return currentRow;
    }

    public void setCurrentRow(ResultSetRow currentRow) {
        this.currentRow = currentRow;
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
     * Put object to cache.
     *
     * @param id   target object id
     * @param data target object
     */
    public void putToCache(Object id, Object data) {
        ((CacheSessionManager) session.getSessionManager()).putToCache(id, data);
    }

    /**
     * Add new object to cache.
     *
     * @param key  target id
     * @param data target object
     */
    public void addEntry(Object key, Object data) {
        cache.putIfAbsent(data.getClass(), new HashMap<>());
        Map<Object, Object> objectCache = cache.get(data.getClass());

        objectCache.put(key, data);
    }

    /**
     * Retrieve object from temporary cache {@link #cache}.
     *
     * @param tClass target object class
     * @param id     target object id
     * @return object from cache or else null if it not exist
     */
    public Object getEntry(Class<?> tClass, Object id) {
        Map<Object, Object> idDataMap = cache.get(tClass);

        if (idDataMap == null) {
            return null;
        }

        return idDataMap.get(id);
    }

    public Set<String> getResultColumns() {
        return resultColumns;
    }

    /**
     * Check is column contains in resultset.
     *
     * @param columnName target column name
     * @return true if column is contains in resultset
     * @throws SQLException any SQL exceptions
     */
    public synchronized boolean isResultColumn(String columnName) throws SQLException {
        return resultColumns.contains(columnName);
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
        private Map<String, ResultSetValue> values;

        /**
         * Read collection object ids from result set.
         */
        private Map<Class<?>, Set<Object>> collectionObjectIds = new HashMap<>();

        /**
         * Entity key.
         */
        private Object key;

        /**
         * Lazy collection owner key.
         */
        private Object lazyCollectionOwnerKey;

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
        public void setValuesFromResultSet(Map<String, ResultSetValue> values) {
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
        public Map<String, ResultSetValue> getValues() {
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
         * @param id              a new read collection object id
         * @param collectionClass target collection class
         */
        public void addCollectionObjectId(Class<?> collectionClass, Object id) {
            if (!collectionObjectIds.containsKey(collectionClass)) {
                collectionObjectIds.put(collectionClass, new LinkedHashSet<>());
            }
            collectionObjectIds.get(collectionClass).add(id);
        }

        /**
         * Return read collection object ids.
         *
         * @param collectionClass target collection class
         * @return read collection object ids
         */
        public Optional<Set<Object>> getCollectionObjectIds(Class<?> collectionClass) {
            return Optional.ofNullable(collectionObjectIds.get(collectionClass));
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

        /**
         * Provide lazy collection owner key.
         *
         * @param key target key
         */
        public void setLazyCollectionOwnerKey(Object key) {
            this.lazyCollectionOwnerKey = key;
        }

        /**
         * Return lazy collection owner key.
         *
         * @return lazy collection owner key
         */
        public Object getLazyCollectionOwnerKey() {
            return lazyCollectionOwnerKey;
        }
    }
}
