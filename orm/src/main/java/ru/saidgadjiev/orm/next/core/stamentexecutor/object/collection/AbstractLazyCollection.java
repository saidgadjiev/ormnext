package ru.saidgadjiev.orm.next.core.stamentexecutor.object.collection;

import ru.saidgadjiev.orm.next.core.dao.Dao;
import ru.saidgadjiev.orm.next.core.stamentexecutor.rowreader.entityinitializer.CollectionLoader;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Created by said on 10.03.2018.
 */
public abstract class AbstractLazyCollection<T> implements Collection<T> {

    private Dao dao;

    private Object ownerId;

    private Collection<T> collection;

    protected boolean initialized = false;

    private CollectionLoader collectionLoader;

    private long cachedSize;

    public AbstractLazyCollection(CollectionLoader collectionLoader, Dao dao, Object ownerId, Collection<T> collection) {
        this.collectionLoader = collectionLoader;
        this.dao = dao;
        this.ownerId = ownerId;
        this.collection = collection;
    }

    protected void read() {
        if (initialized) {
            return;
        }
        try {
            System.out.println("readAll");
            collection.addAll((Collection<? extends T>) collectionLoader.loadCollection(dao, ownerId));
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

        initialized = true;
    }

    protected boolean readSize() {
        if (initialized) {
            return false;
        }
        try {
            System.out.println("readSize");
            cachedSize = collectionLoader.loadSize(dao, ownerId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return true;
    }

    @Override
    public int size() {
        return  readSize() ? (int) cachedSize : collection.size();
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
    public String toString() {
        read();

        return "AbstractLazyCollection{" +
                "collection=" + collection +
                '}';
    }
}
