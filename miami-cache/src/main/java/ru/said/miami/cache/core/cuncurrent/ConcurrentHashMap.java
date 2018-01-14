package ru.said.miami.cache.core.cuncurrent;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ConcurrentHashMap<K, V> extends AbstractMap<K, V> implements ConcurrentMap<K, V> {

    private static final int DEFAULT_CAPACITY = 16;

    protected Node<K, V>[] map;

    private Object[] locks;

    private int capacity;

    /**
     * Максимально допустимое количество элементов в map. Если количество больше то делаем перехеширование. Вычисляется как capacity * loadFactor;
     */
    private int threshold;

    /**
     * Коэфициент загрузки. По умолчанию равен 0.75
     */
    private float loadFactor = 0.75f;

    /**
     * Количество элементов в map
     */
    protected int size = 0;

    private volatile boolean resizing = false;

    static class Node<K, V> implements Map.Entry<K, V> {

        int hash;

        K key;

        V value;

        Node<K, V> next;

        public Node(K key, V value, int hash) {
            this.key = key;
            this.value = value;
            this.hash = hash;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            V oldValue = this.value;

            this.value = value;

            return oldValue;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "key=" + key +
                    ", value=" + value +
                    '}';
        }
    }

    public ConcurrentHashMap(int capacity, float loadFactor) {
        map = newMap(capacity);
        locks = new Object[capacity];
        this.capacity = capacity;
        this.threshold = (int) (capacity * loadFactor);
        initLocks();
    }

    private void initLocks() {
        for (int i = 0; i < capacity; ++i) {
            locks[i] = new Object();
        }
    }

    public ConcurrentHashMap() {
        map = newMap(DEFAULT_CAPACITY);
        this.locks = new Object[DEFAULT_CAPACITY];
        this.capacity = DEFAULT_CAPACITY;
        this.threshold = (int) (DEFAULT_CAPACITY * loadFactor);
        initLocks();
    }

    private Node<K, V>[] newMap(int size) {
        return (Node<K, V>[]) Array.newInstance(Node.class, size);
    }

    @Override
    public V put(K key, V value) {
        int index = indexOf(key);

        synchronized (locks[index]) {
            return putVal(index, key, value);
        }
    }

    protected Node<K, V> newNode(K key, V value, int hash) {
        return new Node<>(key, value, hash);
    }

    protected void afterPut() {}

    protected void afterGet(Node<K, V> node) {}

    protected void afterRemove(Node<K, V> node) {}

    @Override
    public V get(Object key) {
        int index = indexOf(key);

        synchronized (locks[index]) {
            Node<K, V> node = findNode(map[index], key);

            afterGet(node);

            return node != null ? node.value : null;
        }
    }

    private Node<K, V> findNode(Node<K, V> node, Object key) {
        Node<K, V> first = node;

        while (first != null) {
            if (first.key.equals(key)) {
                return first;
            }
            first = first.next;
        }

        return null;
    }

    //TODO: not thread safe
    private void resize() {
        if (!resizing) {
            Node<K, V>[] oldTab = map;

            capacity *= 2;
            size = 0;
            threshold = (int) (capacity * loadFactor);
            map = newMap(capacity);
            for (Node<K, V> entry : oldTab) {
                if (entry != null) {
                    rehashLinks(entry);
                }
            }
            resizing = true;
        }
    }

    private void rehashLinks(Node<K, V> entry) {
        while (entry.next != null) {
            int index = indexOf(entry.key);

            putVal(index, entry.getKey(), entry.getValue());
            entry = entry.next;
        }
    }

    private V putVal(int index, K key, V value) {
        V result = null;

        if (map[index] == null) {
            map[index] = newNode(key, value, index);
        } else {
            Node<K, V> lastNode = findLastNode(map[index]);

            if (lastNode.key.equals(key)) {
                result = lastNode.setValue(value);
            } else {
                lastNode.next = newNode(key, value, index);
            }
        }

        if (++size > threshold) {
            resizing = false;
            synchronized (ConcurrentHashMap.class) {
                resize();
            }
        }
        afterPut();

        return result;
    }

    private Node<K, V> findLastNode(Node<K, V> node) {
        Node<K, V> first = node;

        while (first.next != null) {
            first = first.next;
        }

        return first;
    }

    private int indexOf(Object key) {
        return Math.abs(key.hashCode()) % capacity;
    }

    final Node<K, V> removeNode(int hash, Object key) {
        Node<K, V> temp = map[hash];
        Node<K, V> result = null;

        synchronized (locks[hash]) {
            if (temp == null) { //Если никаких элементов нет то выходим
                return null;
            } else if (temp.key.equals(key)) { //Если элемент первый то возвращаем и удаляем
                map[hash] = temp.next;
                result = temp;
            } else {
                while (temp.next != null) { //Ищем пока не найдем
                    if (temp.next.key.equals(key)) {
                        result = temp.next;
                        temp.next = temp.next.next;
                        break;
                    }
                    temp = temp.next;
                }
            }
            --size;
            afterRemove(result);

            return result;
        }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        Set<Entry<K, V>> result = new HashSet<>();

        result.addAll(Arrays.asList(map));

        return result;
    }

    public V remove(Object key) {
        Node<K, V> node = removeNode(indexOf(key), key);

        return node != null ? node.getValue() : null;
    }

    @Override
    public boolean containsKey(Object key) {
        int index = indexOf(key);

        Node<K, V> node = map[index];

        if (node != null) {
            while (node != null) {
                if (node.key.equals(key)) {
                    return true;
                }
                node = node.next;
            }
        }

        return false;
    }

    @Override
    public V getOrDefault(Object key, V defaultValue) {
        return null;
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {

    }

    @Override
    public boolean remove(Object key, Object value) {
        return false;
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        return false;
    }

    @Override
    public V replace(K key, V oldValue) {
        return null;
    }


    @Override
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {

    }

    @Override
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        return null;
    }

    @Override
    public V putIfAbsent(K key, V value) {
        return null;
    }

    @Override
    public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return null;
    }

    @Override
    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return null;
    }

    @Override
    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        return null;
    }


}
