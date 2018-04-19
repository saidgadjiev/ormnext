package ru.saidgadjiev.orm.next.core.test.model.hibernate;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

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

    @OneToMany(mappedBy = "b", fetch = FetchType.EAGER)
    private Set<D> dSet = new HashSet<>();

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

    public Set<D> getdSet() {
        return dSet;
    }

    public void setdSet(Set<D> dSet) {
        this.dSet = dSet;
    }

    @Override
    public String toString() {
        return "B{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", c=" + c +
                ", a=" + a +
                ", dSet=" + dSet +
                '}';
    }
}
