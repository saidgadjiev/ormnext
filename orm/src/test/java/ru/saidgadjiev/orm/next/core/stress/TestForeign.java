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

    @OneToOne
    @JoinColumn(name = "test_id")
    public TestOneToMany testOneToMany;

    @OneToOne
    @JoinColumn(name = "test_one_to_one_id")
    public TestOneToOne testOneToOne;


}
