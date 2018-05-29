package ru.saidgadjiev.ormnext.core.model;

import ru.saidgadjiev.ormnext.core.field.DatabaseColumn;

public class SimpleEntity {

    @DatabaseColumn(id = true, generated = true)
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimpleEntity that = (SimpleEntity) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
