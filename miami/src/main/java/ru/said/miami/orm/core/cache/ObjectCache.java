package ru.said.miami.orm.core.cache;

/**
 * Created by said on 22.10.17.
 */
public interface ObjectCache {

    <T, ID> void put(ID id, T data);

    <T, ID> T get(ID id);

    <ID> boolean contains(ID id);

    <ID> void invalidate(ID id);

    void invalidateAll();
}
