package ru.saidgadjiev.ormnext.core.loader.object.collection;

import ru.saidgadjiev.ormnext.core.dao.Session;
import ru.saidgadjiev.ormnext.core.loader.object.Lazy;
import ru.saidgadjiev.ormnext.core.logger.Log;
import ru.saidgadjiev.ormnext.core.logger.LoggerFactory;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Base class for implement lazy collection.
 *
 * @param <T> value type
 * @author Said Gadjiev
 */
public abstract class AbstractLazyCollection<T> implements Collection<T>, Lazy {

    /**
     * Logger.
     */
    private static final Log LOG = LoggerFactory.getLogger(AbstractLazyCollection.class);

    /**
     * Current session manager.
     */
    private Session session;

    /**
     * Collection owner id.
     */
    private Object ownerId;

    /**
     * Original collection.
     */
    private Collection<T> collection;

    /**
     * Initialized state.
     */
    private boolean initialized = false;

    /**
     * Collection loader.
     *
     * @see CollectionLoader
     */
    private CollectionLoader collectionLoader;

    /**
     * Cached collection size.
     */
    private long cachedSize;

    /**
     * Create a new lazy collection.
     *
     * @param session target session
     * @param collectionLoader collection loader
     * @param ownerId          owner object id
     * @param collection       original collection
     */
    public AbstractLazyCollection(CollectionLoader collectionLoader,
                                  Session session,
                                  Object ownerId,
                                  Collection<T> collection) {
        this.collectionLoader = collectionLoader;
        this.session = session;
        this.ownerId = ownerId;
        this.collection = collection;
    }

    /**
     * Read collection items and add all to original collection {@link #collection}.
     */
    final void read() {
        if (initialized) {
            return;
        }
        try {
            Collection<?> loadedObjects = collectionLoader.loadCollection(session, ownerId);

            collection.addAll((Collection<? extends T>) loadedObjects);
            Field field = collectionLoader.getForeignCollectionColumnType().getField();

            LOG.debug(
                    "Collection %s lazy initialized with items %s",
                    field.toString(),
                    loadedObjects
            );
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

        initialized = true;
    }

    /**
     * Read collection items size.
     * If already initialized return false, else select size and cache to {@link #cachedSize}.
     *
     * @return false if already initialized
     */
    private boolean readSize() {
        if (initialized) {
            return false;
        }
        try {
            cachedSize = collectionLoader.loadSize(session, ownerId);
            Field field = collectionLoader.getForeignCollectionColumnType().getField();

            LOG.debug(
                    "Collection %s lazy read size %s",
                    field.toString(),
                    cachedSize
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return true;
    }

    @Override
    public int size() {
        return readSize() ? (int) cachedSize : collection.size();
    }

    @Override
    public boolean isEmpty() {
        read();

        return collection.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        read();

        return collection.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        read();

        return collection.iterator();
    }

    @Override
    public Object[] toArray() {
        read();

        return collection.toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        read();

        return collection.toArray(a);
    }

    @Override
    public boolean add(T t) {
        read();

        return collection.add(t);
    }

    @Override
    public boolean remove(Object o) {
        read();

        return collection.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        read();

        return collection.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        read();

        return collection.addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        read();

        return collection.removeAll(c);
    }

    @Override
    public boolean removeIf(Predicate<? super T> filter) {
        read();

        return collection.removeIf(filter);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        read();

        return collection.retainAll(c);
    }

    @Override
    public void clear() {
        read();

        collection.clear();
    }

    @Override
    public boolean equals(Object o) {
        read();

        return collection.equals(o);
    }

    @Override
    public int hashCode() {
        read();

        return collection.hashCode();
    }

    @Override
    public Spliterator<T> spliterator() {
        read();

        return collection.spliterator();
    }

    @Override
    public Stream<T> stream() {
        read();

        return collection.stream();
    }

    @Override
    public Stream<T> parallelStream() {
        read();

        return collection.parallelStream();
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        read();

        collection.forEach(action);
    }

    @Override
    public void attach(Session session) {
        this.session = session;
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public synchronized void setNonInitialized() {
        initialized = false;
        collection.clear();
    }

    @Override
    public String toString() {
        read();

        return "AbstractLazyCollection{"
                + "collection=" + collection
                + '}';
    }
}
