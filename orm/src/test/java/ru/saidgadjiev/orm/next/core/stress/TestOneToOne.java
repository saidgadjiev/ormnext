package ru.saidgadjiev.orm.next.core.stress;

import javax.persistence.*;

/**
 * Created by said on 03.03.2018.
 */
@Entity(name = "testonetoone")
public class TestOneToOne {

    @Id
    @Column(name = "id")
    public int id;

    @Column(name = "name")
    public String name;

    @OneToOne(mappedBy = "testOneToOne")
    public TestForeign testForeign;
}
