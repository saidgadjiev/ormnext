package ru.saidgadjiev.ormnext.core.example.simple;

import ru.saidgadjiev.ormnext.core.field.DatabaseColumn;

import java.lang.invoke.MethodHandles;

public class Test {

    @DatabaseColumn(id = true, generated = true)
    public int id;

    @DatabaseColumn
    private String name;

    public int getId() {
        return id;
    }

    private void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static MethodHandles.Lookup lookup() {
        return MethodHandles.lookup();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Test test = (Test) o;

        if (id != test.id) return false;
        return name != null ? name.equals(test.name) : test.name == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
