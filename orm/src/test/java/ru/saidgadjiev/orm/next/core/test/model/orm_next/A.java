package ru.saidgadjiev.orm.next.core.test.model.orm_next;

import ru.saidgadjiev.orm.next.core.field.DBField;

import javax.persistence.*;

@Entity(name = "a")
public class A {

    @DBField(id = true, generated = true, dataType = 8)
    private int id;

    @DBField(notNull = true)
    private String name;

    @DBField(foreign = true)
    private C c;

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
