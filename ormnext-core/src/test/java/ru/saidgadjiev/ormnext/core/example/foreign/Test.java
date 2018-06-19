package ru.saidgadjiev.ormnext.core.example.foreign;

import ru.saidgadjiev.ormnext.core.field.DatabaseColumn;
import ru.saidgadjiev.ormnext.core.field.ForeignCollectionField;
import ru.saidgadjiev.ormnext.core.field.ForeignColumn;

import java.util.ArrayList;
import java.util.List;

public class Test {

    @DatabaseColumn(id = true, generated = true)
    private int id;

    @DatabaseColumn
    private String name;

    @ForeignColumn
    private TestForeign testForeign;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TestForeign getTestForeign() {
        return testForeign;
    }

    public void setTestForeign(TestForeign testForeign) {
        this.testForeign = testForeign;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Test test = (Test) o;

        if (id != test.id) return false;
        if (name != null ? !name.equals(test.name) : test.name != null) return false;
        return testForeign != null ? testForeign.equals(test.testForeign) : test.testForeign == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (testForeign != null ? testForeign.hashCode() : 0);
        return result;
    }
}
