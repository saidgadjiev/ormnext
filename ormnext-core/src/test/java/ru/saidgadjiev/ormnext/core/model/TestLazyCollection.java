package ru.saidgadjiev.ormnext.core.model;

import ru.saidgadjiev.ormnext.core.field.DatabaseColumn;
import ru.saidgadjiev.ormnext.core.field.FetchType;
import ru.saidgadjiev.ormnext.core.field.ForeignCollectionField;

import java.util.ArrayList;
import java.util.List;

public class TestLazyCollection {

    @DatabaseColumn(id = true, generated = true)
    private int id;

    @ForeignCollectionField(fetchType = FetchType.LAZY)
    private List<TestLazy> entities = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<TestLazy> getEntities() {
        return entities;
    }

    public void setEntities(List<TestLazy> entities) {
        this.entities = entities;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestLazyCollection that = (TestLazyCollection) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
