package ru.saidgadjiev.ormnext.core.stamentexecutor.object.collection;

import ru.saidgadjiev.ormnext.core.dao.Dao;
import ru.saidgadjiev.ormnext.core.stamentexecutor.rowreader.entityinitializer.CollectionLoader;
import ru.saidgadjiev.ormnext.core.dao.Dao;
import ru.saidgadjiev.ormnext.core.stamentexecutor.rowreader.entityinitializer.CollectionLoader;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by said on 09.03.2018.
 */
public class LazyList<T> extends AbstractLazyCollection<T> implements List<T> {

    private List<T> list;

    public LazyList(CollectionLoader collectionLoader, Dao dao, Object ownerId, List<T> list) {
        super(collectionLoader, dao, ownerId, list);
        this.list = list;
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        read();

        return list.addAll(index, c);
    }

    @Override
    public T get(int index) {
        read();

        return list.get(index);
    }

    @Override
    public T set(int index, T element) {
        read();

        return list.set(index, element);
    }

    @Override
    public void add(int index, T element) {
        read();

        list.add(index, element);
    }

    @Override
    public T remove(int index) {
        read();

        return list.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        read();

        return list.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        read();

        return list.lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() {
        read();

        return list.listIterator();
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        read();

        return list.listIterator(index);
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        read();

        return list.subList(fromIndex, toIndex);
    }
}
