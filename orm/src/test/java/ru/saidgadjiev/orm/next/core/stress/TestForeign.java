package ru.saidgadjiev.orm.next.core.stress;

import javax.persistence.*;

/**
 * Created by said on 02.03.2018.
 */

@Entity(name = "testforeign")
public class TestForeign {
    @Id
    @Column(name = "id")
    public int id;

    @Column(name = "name")
    public String name;

    @ManyToOne
    @JoinColumn(name = "test_id")
    public TestOneToMany testOneToMany;

}
