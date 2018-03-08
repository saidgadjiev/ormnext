package ru.saidgadjiev.orm.next.core.stress;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by said on 02.03.2018.
 */
@Entity(name = "testonetomany")
public class TestOneToMany {

    @Id
    @Column(name = "id")
    public int id;

    @Column(name = "name")
    public String name;

    @OneToMany(mappedBy = "testOneToMany", fetch = FetchType.LAZY)
    public List<TestForeign> testForeign;

    public TestOneToMany() {
    }

    public List<TestForeign> getTestForeign() {
        return testForeign;
    }

    public void setTestForeign(List<TestForeign> testForeign) {
        this.testForeign = testForeign;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
