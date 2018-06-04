package ru.saidgadjiev.ormnext.core.example.foreigncollection;

import ru.saidgadjiev.ormnext.core.field.DatabaseColumn;
import ru.saidgadjiev.ormnext.core.field.ForeignColumn;

public class TestForeignCollection {

    @DatabaseColumn(id = true, generated = true)
    private int id;

    @DatabaseColumn
    private String name;

    @ForeignColumn
    private Test test;

    public void setTest(Test test) {
        this.test = test;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestForeignCollection that = (TestForeignCollection) o;

        if (id != that.id) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return test != null ? test.equals(that.test) : that.test == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (test != null ? test.hashCode() : 0);
        return result;
    }
}
