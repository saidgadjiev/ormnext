package ru.saidgadjiev.orm.next.core.test.model.orm_next;

import ru.saidgadjiev.orm.next.core.field.DatabaseColumn;

import javax.persistence.*;

@Entity(name = "c")
public class C {

    @DatabaseColumn(id = true, generated = true, dataType = 8)
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

}
