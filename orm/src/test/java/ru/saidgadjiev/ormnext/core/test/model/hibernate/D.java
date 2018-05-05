package ru.saidgadjiev.ormnext.core.test.model.hibernate;

import javax.persistence.*;

@Entity(name = "d")
public class D {

    @Id
    @Column
    private int id;

    @Column(nullable = false)
    private String name;

    @OneToOne(fetch = FetchType.EAGER)
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
