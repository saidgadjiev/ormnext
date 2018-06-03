package ru.saidgadjiev.ormnext.core.model;

import ru.saidgadjiev.ormnext.core.field.DatabaseColumn;
import ru.saidgadjiev.ormnext.core.field.ForeignCollectionField;
import ru.saidgadjiev.ormnext.core.field.ForeignColumn;

import java.util.ArrayList;
import java.util.List;

public class ForeignAutoCreateForeignCollectionColumnTestEntity {

    @DatabaseColumn(id = true, generated = true)
    private int id;

    @DatabaseColumn
    private String desc;

    @ForeignCollectionField(foreignAutoCreate = true)
    private List<TestEntity> entities = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public List<TestEntity> getEntities() {
        return entities;
    }

    public void setEntities(List<TestEntity> entities) {
        this.entities = entities;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ForeignAutoCreateForeignCollectionColumnTestEntity that = (ForeignAutoCreateForeignCollectionColumnTestEntity) o;

        if (id != that.id) return false;
        return desc != null ? desc.equals(that.desc) : that.desc == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (desc != null ? desc.hashCode() : 0);
        return result;
    }
}
