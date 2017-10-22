package ru.said.miami.cache.core.cuncurrent;

/**
 * Алгоритм кеширования LRU
 * @param <K>
 * @param <V>
 */
public class LRUCacheMap<K, V> extends AbstractCacheMap<K, V> {

    private LinkedEntry<K, V> head;

    private LinkedEntry<K, V> tail;

    private int maxSize;

    static class LinkedEntry<K, V> extends Node<K, V> {

        LinkedEntry<K, V> prev;
        
        LinkedEntry<K, V> after;
 
        public LinkedEntry(K key, V value, int hash) {
            super(key, value, hash);
        }
    }

    public LRUCacheMap(int maxSize) {
        super();
        this.maxSize = maxSize;
    }

    public LRUCacheMap(int capacity, float loadFactor, int maxSize ) {
        super(capacity, loadFactor);

        this.maxSize = maxSize;
    }

    @Override
    protected AbstractCacheMap.Node<K, V> newNode(K key, V value, int hash) {
        LinkedEntry<K, V> entry = new LinkedEntry<>(key, value, hash);

        linkEntry(entry);

        return entry;
    }

    //Делаем связи
    private void linkEntry(LinkedEntry<K, V> entry) {
        LinkedEntry<K,V> last = tail;

        tail = entry;

        if (last == null) {
            head = entry;
        } else {
            entry.prev = last;
            last.after = entry;
        }
    }

    //Удаляем связи
    private void unlinkEntry(LinkedEntry<K, V> entry) {
        LinkedEntry<K,V> tmp = entry, prev = tmp.prev, after = tmp.after;

        tmp.prev = tmp.after = null;

        if (prev == null) {
            head = after;
        } else {
            prev.after = after;
        }
        if (after == null) {
            tail = prev;
        } else {
            after.prev = prev;
        }
    }

    //Удаляем само старое значение если размер больше заданного
    @Override
    protected void afterPut() {
        if (maxSize < size()) {
            removeNode(head.hash, head.key);
        }
    }

    //Передвигаем запрашиваемый элемент в конец списка
    @Override
    protected void afterGet(Node<K, V> node) {
        LinkedEntry<K,V> last = null;

        if ((last = tail) != node) {
            LinkedEntry<K, V> entry = (LinkedEntry<K, V>) node, prev = entry.prev, after = entry.after;

            entry.after = null;
            if (prev == null) {
                head = after;
            } else {
                prev.after = after;
            }
            if (after != null) {
                after.prev = prev;
            } else {
                last = prev;
            }
            if (last == null) {
                head = entry;
            } else {
                entry.prev = last;
                last.after = entry;
            }
            tail = entry;
        }
    }

    @Override
    protected void afterRemove(Node<K, V> node) {
        unlinkEntry((LinkedEntry<K, V>) node);
    }
}
