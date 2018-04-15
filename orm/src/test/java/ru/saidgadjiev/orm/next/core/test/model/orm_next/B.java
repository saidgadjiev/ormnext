package ru.saidgadjiev.orm.next.core.test.model.orm_next;

import ru.saidgadjiev.orm.next.core.field.DatabaseColumn;
import ru.saidgadjiev.orm.next.core.field.ForeignColumn;

import javax.persistence.*;

@Entity(name = "b")
public class B {

    @DatabaseColumn(id = true, generated = true, dataType = 8)
    private int id;

    @DatabaseColumn(notNull = true)
    private String name;

    @ForeignColumn
    private C c;

    @ForeignColumn
    private A a;

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
}
