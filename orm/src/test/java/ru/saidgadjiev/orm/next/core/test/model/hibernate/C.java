package ru.saidgadjiev.orm.next.core.test.model.hibernate;

import javax.persistence.*;

@Entity(name = "c")
public class C {

    @Id
    @Column
    private int id;

    @Column(nullable = false)
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
