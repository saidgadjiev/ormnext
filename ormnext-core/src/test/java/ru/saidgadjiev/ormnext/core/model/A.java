package ru.saidgadjiev.ormnext.core.model;

import ru.saidgadjiev.ormnext.core.field.DatabaseColumn;
import ru.saidgadjiev.ormnext.core.field.ForeignColumn;

public class A {

    @DatabaseColumn(id = true, generated = true)
    private int id;

    @DatabaseColumn
    private String desc;

    @ForeignColumn
    private ForeignCollectionTestEntity foreignCollectionTestEntity;

    @ForeignColumn
    private ForeignAutoCreateForeignCollectionColumnTestEntity foreignAutoCreateForeignCollectionColumnTestEntity;

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

    public ForeignCollectionTestEntity getForeignCollectionTestEntity() {
        return foreignCollectionTestEntity;
    }

    public void setForeignCollectionTestEntity(ForeignCollectionTestEntity foreignCollectionTestEntity) {
        this.foreignCollectionTestEntity = foreignCollectionTestEntity;
    }

    public ForeignAutoCreateForeignCollectionColumnTestEntity getForeignAutoCreateForeignCollectionColumnTestEntity() {
        return foreignAutoCreateForeignCollectionColumnTestEntity;
    }

    public void setForeignAutoCreateForeignCollectionColumnTestEntity(ForeignAutoCreateForeignCollectionColumnTestEntity foreignAutoCreateForeignCollectionColumnTestEntity) {
        this.foreignAutoCreateForeignCollectionColumnTestEntity = foreignAutoCreateForeignCollectionColumnTestEntity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        A entity = (A) o;

        if (id != entity.id) return false;
        if (desc != null ? !desc.equals(entity.desc) : entity.desc != null) return false;
        if (foreignCollectionTestEntity != null ? !foreignCollectionTestEntity.equals(entity.foreignCollectionTestEntity) : entity.foreignCollectionTestEntity != null)
            return false;
        return foreignAutoCreateForeignCollectionColumnTestEntity != null ? foreignAutoCreateForeignCollectionColumnTestEntity.equals(entity.foreignAutoCreateForeignCollectionColumnTestEntity) : entity.foreignAutoCreateForeignCollectionColumnTestEntity == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (desc != null ? desc.hashCode() : 0);
        result = 31 * result + (foreignCollectionTestEntity != null ? foreignCollectionTestEntity.hashCode() : 0);
        result = 31 * result + (foreignAutoCreateForeignCollectionColumnTestEntity != null ? foreignAutoCreateForeignCollectionColumnTestEntity.hashCode() : 0);
        return result;
    }
}
