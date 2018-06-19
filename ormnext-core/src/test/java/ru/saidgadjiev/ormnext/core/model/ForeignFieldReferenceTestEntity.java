package ru.saidgadjiev.ormnext.core.model;

import ru.saidgadjiev.ormnext.core.field.DatabaseColumn;
import ru.saidgadjiev.ormnext.core.field.ForeignColumn;

public class ForeignFieldReferenceTestEntity {

    @DatabaseColumn(id = true, generated = true)
    private int id;

    @ForeignColumn(foreignFieldName = "name")
    private UniqueFieldTestEntity uniqueFieldTestEntity;

    @ForeignColumn(foreignFieldName = "name")
    private TableUniqueFieldTestEntity tableUniqueFieldTestEntity;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UniqueFieldTestEntity getUniqueFieldTestEntity() {
        return uniqueFieldTestEntity;
    }

    public void setUniqueFieldTestEntity(UniqueFieldTestEntity uniqueFieldTestEntity) {
        this.uniqueFieldTestEntity = uniqueFieldTestEntity;
    }

    public TableUniqueFieldTestEntity getTableUniqueFieldTestEntity() {
        return tableUniqueFieldTestEntity;
    }

    public void setTableUniqueFieldTestEntity(TableUniqueFieldTestEntity tableUniqueFieldTestEntity) {
        this.tableUniqueFieldTestEntity = tableUniqueFieldTestEntity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ForeignFieldReferenceTestEntity that = (ForeignFieldReferenceTestEntity) o;

        if (id != that.id) return false;
        if (uniqueFieldTestEntity != null ? !uniqueFieldTestEntity.equals(that.uniqueFieldTestEntity) : that.uniqueFieldTestEntity != null)
            return false;
        return tableUniqueFieldTestEntity != null ? tableUniqueFieldTestEntity.equals(that.tableUniqueFieldTestEntity) : that.tableUniqueFieldTestEntity == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (uniqueFieldTestEntity != null ? uniqueFieldTestEntity.hashCode() : 0);
        result = 31 * result + (tableUniqueFieldTestEntity != null ? tableUniqueFieldTestEntity.hashCode() : 0);
        return result;
    }
}
