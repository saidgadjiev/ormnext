package ru.saidgadjiev.ormnext.core.model;

import ru.saidgadjiev.ormnext.core.field.DatabaseColumn;
import ru.saidgadjiev.ormnext.core.table.DatabaseEntity;
import ru.saidgadjiev.ormnext.core.table.Unique;

@DatabaseEntity(
        uniqueConstraints = {
                @Unique(columns = {"name"})
        }
)
public class TableUniqueFieldTestEntity {

    @DatabaseColumn(id = true, generated = true)
    private int id;

    @DatabaseColumn(notNull = true)
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TableUniqueFieldTestEntity that = (TableUniqueFieldTestEntity) o;

        if (id != that.id) return false;
        return name != null ? name.equals(that.name) : that.name == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
