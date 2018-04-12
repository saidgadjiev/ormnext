package ru.saidgadjiev.orm.next.core.test.model.hibernate;

import javax.persistence.*;

@Entity(name = "b")
public class B {

    @Id
    @Column
    private int id;

    @Column(nullable = false)
    private String name;

    @OneToOne(fetch = FetchType.EAGER)
    private C c;

    @OneToOne(fetch = FetchType.EAGER)
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

    public C getC() {
        return c;
    }

    public void setC(C c) {
        this.c = c;
    }

    public A getA() {
        return a;
    }

    public void setA(A a) {
        this.a = a;
    }

    @Override
    public String toString() {
        return "B{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", c=" + c +
                ", a=" + a +
                '}';
    }
}
