package ru.saidgadjiev.ormnext.core.model;

import ru.saidgadjiev.ormnext.core.field.DatabaseColumn;
import ru.saidgadjiev.ormnext.core.field.FetchType;
import ru.saidgadjiev.ormnext.core.field.ForeignColumn;

public class TestLazyForeign {

    @DatabaseColumn(id = true, generated = true)
    private int id;

    @DatabaseColumn
    private String desc;

    @ForeignColumn(fetchType = FetchType.LAZY)
    private TestLazyCollection testLazyCollection;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public TestLazyCollection getTestLazyCollection() {
        return testLazyCollection;
    }

    public void setTestLazyCollection(TestLazyCollection testLazyCollection) {
        this.testLazyCollection = testLazyCollection;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestLazyForeign testLazy = (TestLazyForeign) o;

        if (id != testLazy.id) return false;
        if (desc != null ? !desc.equals(testLazy.desc) : testLazy.desc != null) return false;
        return testLazyCollection != null ? testLazyCollection.equals(testLazy.testLazyCollection) : testLazy.testLazyCollection == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (desc != null ? desc.hashCode() : 0);
        result = 31 * result + (testLazyCollection != null ? testLazyCollection.hashCode() : 0);
        return result;
    }
}
