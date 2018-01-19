package ru.said.orm.next.core.cache;

public interface ObjectCache {

    <T, ID> void put(ID id, T data);

    <T, ID> T get(ID id);

    <ID> boolean contains(ID id);

    <ID> void invalidate(ID id);

    void invalidateAll();
}
