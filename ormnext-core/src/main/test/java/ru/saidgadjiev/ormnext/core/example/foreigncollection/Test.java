package ru.saidgadjiev.ormnext.core.example.foreigncollection;

import ru.saidgadjiev.ormnext.core.field.DatabaseColumn;
import ru.saidgadjiev.ormnext.core.field.ForeignCollectionField;

import java.util.ArrayList;
import java.util.List;

public class Test {

    @DatabaseColumn(id = true, generated = true)
    private int id;

    @ForeignCollectionField
    private List<TestForeignCollection> testForeigns = new ArrayList<>();

    public void addTestForeign(TestForeignCollection testForeign) {
        this.testForeigns.add(testForeign);
    }

    public List<TestForeignCollection> getTestForeigns() {
        return testForeigns;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Test test = (Test) o;

        return id == test.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
