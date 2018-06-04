package ru.saidgadjiev.ormnext.core.example.foreign;

import ru.saidgadjiev.ormnext.core.field.DatabaseColumn;

public class TestForeign {

    @DatabaseColumn(id = true, generated = true)
    private int id;

    @DatabaseColumn
    private String name;

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestForeign that = (TestForeign) o;

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
