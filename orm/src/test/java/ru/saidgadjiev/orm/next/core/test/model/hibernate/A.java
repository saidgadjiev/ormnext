package ru.saidgadjiev.orm.next.core.test.model.hibernate;

import javax.persistence.*;

@Entity(name = "a")
public class A {

    @Id
    @Column
    private int id;

    @Column(nullable = false)
    private String name;

    @OneToOne(fetch = FetchType.EAGER)
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

    public C getC() {
        return c;
    }

    public void setC(C c) {
        this.c = c;
    }

    @Override
    public String toString() {
        return "A{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", c=" + c +
                '}';
    }
}
