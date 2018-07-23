package ru.saidgadjiev.ormnext.core.model;

import ru.saidgadjiev.ormnext.core.field.DatabaseColumn;
import ru.saidgadjiev.ormnext.core.field.ForeignColumn;

public class ForeignAutoCreateForeignColumnTestEntity {

    @DatabaseColumn(id = true, generated = true)
    private int id;

    @DatabaseColumn
    private String desc;

    @ForeignColumn(foreignAutoCreate = true)
    private A a;

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

    public A getA() {
        return a;
    }

    public void setA(A a) {
        this.a = a;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ForeignAutoCreateForeignColumnTestEntity that = (ForeignAutoCreateForeignColumnTestEntity) o;

        if (id != that.id) return false;
        if (desc != null ? !desc.equals(that.desc) : that.desc != null) return false;
        return a != null ? a.equals(that.a) : that.a == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (desc != null ? desc.hashCode() : 0);
        result = 31 * result + (a != null ? a.hashCode() : 0);
        return result;
    }
}
