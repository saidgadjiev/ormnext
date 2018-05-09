package ru.saidgadjiev.ormnext.core.test.model.ormnext;

import ru.saidgadjiev.ormnext.core.field.DatabaseColumn;
import ru.saidgadjiev.ormnext.core.field.ForeignColumn;

public class D {

    @DatabaseColumn(id = true, generated = true, dataType = 12)
    private int id;

    @DatabaseColumn(notNull = true)
    private String name;

    @ForeignColumn
    private B b;

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

    public B getB() {
        return b;
    }

    public void setB(B b) {
        this.b = b;
    }

    @Override
    public String toString() {
        return "D{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
