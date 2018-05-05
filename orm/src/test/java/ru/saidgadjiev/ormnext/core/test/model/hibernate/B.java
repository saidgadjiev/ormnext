package ru.saidgadjiev.ormnext.core.test.model.hibernate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "b")
public class B {

    @Id
    @Column
    private int id;

    @Column(nullable = false)
    private String name;

    @OneToOne(fetch = FetchType.LAZY)
    private C c;

    @OneToOne(fetch = FetchType.EAGER)
    private A a;

    @OneToMany(mappedBy = "b", fetch = FetchType.LAZY)
    private List<D> dSet = new ArrayList<>();

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

    public List<D> getdSet() {
        return dSet;
    }

    public void setdSet(ArrayList<D> dSet) {
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
