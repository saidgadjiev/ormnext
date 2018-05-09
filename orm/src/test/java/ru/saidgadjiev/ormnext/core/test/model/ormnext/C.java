package ru.saidgadjiev.ormnext.core.test.model.ormnext;

import ru.saidgadjiev.ormnext.core.field.DatabaseColumn;


public class C {

    @DatabaseColumn(id = true, generated = true, dataType = 12)
    private int id;

    @DatabaseColumn(notNull = true, defaultDefinition = "'said'")
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
    public String toString() {
        return "C{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
