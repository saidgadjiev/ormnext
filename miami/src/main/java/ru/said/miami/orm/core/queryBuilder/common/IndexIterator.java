package ru.said.miami.orm.core.queryBuilder.common;

/**
 * Created by said on 14.01.2018.
 */
public class IndexIterator {

    private int index = 0;

    public IndexIterator(int index) {
        this.index = index;
    }

    public IndexIterator() {

    }

    public int nextId() {
        return index++;
    }

    public void reset() {
        index = 0;
    }

    public int last() {
        return index;
    }
}
