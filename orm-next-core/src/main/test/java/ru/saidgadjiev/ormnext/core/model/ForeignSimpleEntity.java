package ru.saidgadjiev.ormnext.core.model;

import ru.saidgadjiev.ormnext.core.field.DatabaseColumn;
import ru.saidgadjiev.ormnext.core.field.ForeignColumn;

public class ForeignSimpleEntity {

    @DatabaseColumn(id = true, generated = true)
    private int id;

    @ForeignColumn
    private SimpleEntity entity;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public SimpleEntity getEntity() {
        return entity;
    }

    public void setEntity(SimpleEntity entity) {
        this.entity = entity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ForeignSimpleEntity that = (ForeignSimpleEntity) o;

        if (id != that.id) return false;
        return entity != null ? entity.equals(that.entity) : that.entity == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (entity != null ? entity.hashCode() : 0);
        return result;
    }
}
