package ru.saidgadjiev.ormnext.core.example.foreign;

import ru.saidgadjiev.ormnext.core.field.DatabaseColumn;
import ru.saidgadjiev.ormnext.core.field.ForeignColumn;

public class Test {

    @DatabaseColumn(id = true, generated = true)
    private int id;

    @ForeignColumn
    private TestForeign testForeign;

    public void setTestForeign(TestForeign testForeign) {
        this.testForeign = testForeign;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Test test = (Test) o;

        if (id != test.id) return false;
        return testForeign != null ? testForeign.equals(test.testForeign) : test.testForeign == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (testForeign != null ? testForeign.hashCode() : 0);
        return result;
    }
}
